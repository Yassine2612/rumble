/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Stefan Irimescu, Can Berker Cikis
 *
 */

package org.rumbledb.runtime.flwor.clauses;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.rumbledb.api.Item;
import org.rumbledb.context.DynamicContext;
import org.rumbledb.context.Name;
import org.rumbledb.context.DynamicContext.VariableDependency;
import org.rumbledb.exceptions.ExceptionMetadata;
import org.rumbledb.exceptions.IteratorFlowException;
import org.rumbledb.exceptions.JobWithinAJobException;
import org.rumbledb.exceptions.UnsupportedFeatureException;
import org.rumbledb.expressions.ExecutionMode;
import org.rumbledb.runtime.RuntimeIterator;
import org.rumbledb.runtime.RuntimeTupleIterator;
import org.rumbledb.runtime.flwor.FlworDataFrameUtils;
import org.rumbledb.runtime.flwor.udfs.GroupClauseSerializeAggregateResultsUDF;
import org.rumbledb.runtime.flwor.udfs.HashUDF;
import org.rumbledb.runtime.flwor.udfs.LetClauseUDF;
import org.rumbledb.runtime.operational.ComparisonOperationIterator;
import org.rumbledb.runtime.postfix.PredicateIterator;
import org.rumbledb.runtime.primary.VariableReferenceIterator;
import org.rumbledb.types.SequenceType;

import sparksoniq.jsoniq.tuple.FlworTuple;
import sparksoniq.spark.SparkSessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LetClauseSparkIterator extends RuntimeTupleIterator {


    private static final long serialVersionUID = 1L;
    private Name variableName; // for efficient use in local iteration
    private RuntimeIterator assignmentIterator;
    private DynamicContext tupleContext; // re-use same DynamicContext object for efficiency
    private FlworTuple nextLocalTupleResult;

    public LetClauseSparkIterator(
            RuntimeTupleIterator child,
            Name variableName,
            RuntimeIterator assignmentIterator,
            ExecutionMode executionMode,
            ExceptionMetadata iteratorMetadata
    ) {
        super(child, executionMode, iteratorMetadata);
        this.variableName = variableName;
        this.assignmentIterator = assignmentIterator;
    }

    @Override
    public void open(DynamicContext context) {
        super.open(context);
        if (this.child == null) {
            this.nextLocalTupleResult = generateTupleFromExpressionWithContext(null, this.currentDynamicContext);
        } else {
            this.child.open(this.currentDynamicContext);
            this.tupleContext = new DynamicContext(this.currentDynamicContext); // assign current context as parent
            setNextLocalTupleResult();
        }
    }

    @Override
    public void reset(DynamicContext context) {
        super.reset(context);
        if (this.child == null) {
            this.nextLocalTupleResult = generateTupleFromExpressionWithContext(null, this.currentDynamicContext);
        } else {
            this.child.reset(this.currentDynamicContext);
            this.tupleContext = new DynamicContext(this.currentDynamicContext); // assign current context as parent
            setNextLocalTupleResult();
        }
    }

    private void setNextLocalTupleResult() {
        // if starting clause: result is a single tuple -> no more tuples after the first next call
        if (this.child == null) {
            this.hasNext = false;
            return;
        }

        if (this.child.hasNext()) {
            FlworTuple inputTuple = this.child.next();
            this.tupleContext.getVariableValues().removeAllVariables(); // clear the previous variables
            this.tupleContext.getVariableValues().setBindingsFromTuple(inputTuple, getMetadata()); // assign new
                                                                                                   // variables from new
                                                                                                   // tuple

            this.nextLocalTupleResult = generateTupleFromExpressionWithContext(inputTuple, this.tupleContext);
            this.hasNext = true;
        } else {
            this.child.close();
            this.hasNext = false;
        }
    }

    private FlworTuple generateTupleFromExpressionWithContext(FlworTuple inputTuple, DynamicContext context) {
        FlworTuple resultTuple;
        if (inputTuple == null) {
            resultTuple = new FlworTuple();
        } else {
            resultTuple = new FlworTuple(inputTuple);
        }
        if (this.assignmentIterator.isDataFrame()) {
            Dataset<Row> df = this.assignmentIterator.getDataFrame(context);
            resultTuple.putValue(this.variableName, df);
        } else if (this.assignmentIterator.isRDD()) {
            JavaRDD<Item> itemRDD = this.assignmentIterator.getRDD(context);
            resultTuple.putValue(this.variableName, itemRDD);
        } else {
            List<Item> results = new ArrayList<>();
            this.assignmentIterator.open(context);
            while (this.assignmentIterator.hasNext()) {
                results.add(this.assignmentIterator.next());
            }
            this.assignmentIterator.close();
            resultTuple.putValue(this.variableName, results);
        }
        return resultTuple;
    }

    @Override
    public FlworTuple next() {
        if (this.hasNext) {
            FlworTuple result = this.nextLocalTupleResult; // save the result to be returned
            setNextLocalTupleResult(); // calculate and store the next result
            return result;
        }
        throw new IteratorFlowException("Invalid next() call in let flwor clause", getMetadata());
    }

    @Override
    public void close() {
        this.isOpen = false;
        if (this.child != null) {
            this.child.close();
        }
    }

    @Override
    public Dataset<Row> getDataFrame(
            DynamicContext context,
            Map<Name, DynamicContext.VariableDependency> parentProjection
    ) {
        if (this.child != null) {
            Dataset<Row> df = this.child.getDataFrame(context, getProjection(parentProjection));

            if (!parentProjection.containsKey(this.variableName)) {
                return df;
            }

            if (this.assignmentIterator.isRDD()) {
                return getDataFrameAsJoin(context, parentProjection, df);
            }

            df = bindLetVariableInDataFrame(
                df,
                this.variableName,
                this.assignmentIterator,
                context,
                (this.child == null)?Collections.emptyList():new ArrayList<Name>(this.child.getOutputTupleVariableNames()),
                parentProjection,
                false
            );

            return df;
        }
        throw new RuntimeException(
                "Unexpected program state reached. Initial let clauses are always locally executed."
        );
    }

    public Dataset<Row> getDataFrameAsJoin(
            DynamicContext context,
            Map<Name, DynamicContext.VariableDependency> parentProjection,
            Dataset<Row> childDF
    ) {
        // We try to detect an equi-join.

        // Is this a predicate expression?
        if (!(this.assignmentIterator instanceof PredicateIterator)) {
            throw new JobWithinAJobException(
                    "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. Rumble is able to handle large scale left outer joins, but this requires the let expression to be a predicate expression, the left-hand-side of which is independent from the previous variables of the current FLWOR expression.",
                    getMetadata()
            );
        }

        RuntimeIterator sequenceIterator = ((PredicateIterator) this.assignmentIterator).sequenceIterator();
        RuntimeIterator predicateIterator = ((PredicateIterator) this.assignmentIterator).predicateIterator();

        // Is the left-hand-side of this predicate expression independent from input tuples?
        if (!isExpressionIndependentFromInputTuple(sequenceIterator, this.child)) {
            throw new JobWithinAJobException(
                    "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. Rumble attempted to detect a join but the left-hand-side of the predicate expression in this let clause depends on the previous variables of the current FLWOR expression. You can try again by making sure that such is not the case.",
                    getMetadata()
            );
        }

        // Is the predicate a comparison?
        if (!(predicateIterator instanceof ComparisonOperationIterator)) {
            throw new JobWithinAJobException(
                    "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. We did detect a predicate expression, but the criterion inside the predicate is not a comparison.",
                    getMetadata()
            );

        }
        ComparisonOperationIterator comparisonIterator = (ComparisonOperationIterator) predicateIterator;
        // Is the predicate a value equality comparison?
        if (!comparisonIterator.isValueEquality()) {
            throw new JobWithinAJobException(
                    "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. We did detect a predicate expression, but the criterion inside the predicate is not a value equality comparison.",
                    getMetadata()
            );
        }

        // Is the equality comparing the left hand side of the predicate with the input tuple?
        RuntimeIterator leftHandSideOfJoinEqualityCriterion = comparisonIterator.getLeftIterator();
        RuntimeIterator rightHandSideOfJoinEqualityCriterion = comparisonIterator.getRightIterator();
        Set<Name> leftDependencies = new HashSet<>(
                leftHandSideOfJoinEqualityCriterion.getVariableDependencies().keySet()
        );
        Set<Name> rightDependencies = new HashSet<>(
                rightHandSideOfJoinEqualityCriterion.getVariableDependencies().keySet()
        );
        RuntimeIterator contextItemValueExpression = null;
        RuntimeIterator inputTupleValueExpression = null;
        if (leftDependencies.size() == 1 && leftDependencies.contains(Name.CONTEXT_ITEM)) {
            if (!rightDependencies.contains(Name.CONTEXT_ITEM)) {
                contextItemValueExpression = leftHandSideOfJoinEqualityCriterion;
                inputTupleValueExpression = rightHandSideOfJoinEqualityCriterion;
            } else {
                throw new JobWithinAJobException(
                        "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. We did detect a predicate expression, but the criterion inside the predicate is not comparing the left-hand-side of this predicate to the input tuple.",
                        getMetadata()
                );
            }
        }
        if (rightDependencies.size() == 1 && rightDependencies.contains(Name.CONTEXT_ITEM)) {
            if (!leftDependencies.contains(Name.CONTEXT_ITEM)) {
                contextItemValueExpression = rightHandSideOfJoinEqualityCriterion;
                inputTupleValueExpression = leftHandSideOfJoinEqualityCriterion;
            } else {
                throw new JobWithinAJobException(
                        "A let clause expression cannot produce a big sequence of items for a big number of tuples, as this would lead to a data flow explosion. We did detect a predicate expression, but the criterion inside the predicate is not comparing the left-hand-side of this predicate to the input tuple.",
                        getMetadata()
                );
            }
        }

        // Now we know we can execute the query as an equi-join.
        // First, we evaluate all input tuples.
        Dataset<Row> inputDF = this.child.getDataFrame(context, getProjection(parentProjection));

        // We resolve the dependencies of the predicate expression.
        // We need to manually adjust the context item with the dependency mode the parent projection.
        Map<Name, VariableDependency> predicateDependencies = predicateIterator.getVariableDependencies();
        if (parentProjection.containsKey(this.variableName)) {
            predicateDependencies.put(Name.CONTEXT_ITEM, parentProjection.get(this.variableName));
        }

        // If the predicate depends on position() or last(), we are not able yet to support this.
        if (predicateDependencies.containsKey(Name.CONTEXT_POSITION)) {
            throw new UnsupportedFeatureException(
                    "Rumble detected an equi-join, but does not support yet position() in the join predicate.",
                    getMetadata()
            );
        }
        if (predicateDependencies.containsKey(Name.CONTEXT_COUNT)) {
            throw new UnsupportedFeatureException(
                    "Rumble detected an equi-join, but does not support yet last() in the join predicate.",
                    getMetadata()
            );
        }

        // Now we execute the left-hand-side of the predicate, which is the right side of the join.
        Dataset<Row> expressionDF = ForClauseSparkIterator.getDataFrameStartingClause(
            sequenceIterator,
            Name.CONTEXT_ITEM,
            null,
            false,
            context,
            predicateDependencies
        );

        System.out.println("[INFO] Rumble detected an equi-join in the left clause.");

        // We compute the hashes for both sides of the equality predicate.
        expressionDF = LetClauseSparkIterator.bindLetVariableInDataFrame(
            expressionDF,
            Name.createVariableInNoNamespace(SparkSessionManager.expressionHashColumnName),
            contextItemValueExpression,
            context,
            (this.child == null)?Collections.emptyList():new ArrayList<Name>(this.child.getOutputTupleVariableNames()),
            null,
            true
        );
        inputDF = LetClauseSparkIterator.bindLetVariableInDataFrame(
            inputDF,
            Name.createVariableInNoNamespace(SparkSessionManager.inputTupleHashColumnName),
            inputTupleValueExpression,
            context,
            (this.child == null)?Collections.emptyList():new ArrayList<Name>(this.child.getOutputTupleVariableNames()),
            null,
            true
        );

        // We group the right-hand-side of the join by hash to prepare the left outer join.
        expressionDF.createOrReplaceTempView("hashedExpressionResults");
        expressionDF = expressionDF.sparkSession()
            .sql(
                String.format(
                    "SELECT `%s`, collect_list(`%s`) AS `%s` FROM hashedExpressionResults GROUP BY `%s`",
                    SparkSessionManager.expressionHashColumnName,
                    Name.CONTEXT_ITEM.toString(),
                    this.variableName,
                    SparkSessionManager.expressionHashColumnName
                )
            );

        // We serialize back all grouped items as sequences of items.
        expressionDF.createOrReplaceTempView("groupedResults");
        expressionDF.sparkSession()
            .udf()
            .register(
                "serializeArray",
                new GroupClauseSerializeAggregateResultsUDF(),
                DataTypes.BinaryType
            );
        expressionDF = expressionDF.sparkSession()
            .sql(
                String.format(
                    "SELECT `%s`, serializeArray(`%s`) AS `%s` FROM groupedResults",
                    SparkSessionManager.expressionHashColumnName,
                    this.variableName,
                    this.variableName,
                    SparkSessionManager.expressionHashColumnName
                )
            );

        expressionDF.createOrReplaceTempView("groupedAndSerializedResults");
        inputDF.createOrReplaceTempView("inputTuples");

        // We gather the columns to select.
        // We need to project away the let clause variable because we re-create it.
        StructType inputSchema = inputDF.schema();
        List<Name> variableNamesToExclude = new ArrayList<>();
        variableNamesToExclude.add(this.variableName);
        List<String> columnsToSelect = FlworDataFrameUtils.getColumnNames(
            inputSchema,
            parentProjection,
            null,
            variableNamesToExclude
        );
        String projectionVariables = FlworDataFrameUtils.getSQLProjection(columnsToSelect, true);

        // Now we proceed with the left outer join.
        inputDF = inputDF.sparkSession()
            .sql(
                String.format(
                    "SELECT %s groupedAndSerializedResults.`%s` AS `%s` FROM inputTuples LEFT OUTER JOIN groupedAndSerializedResults ON `%s` = `%s`",
                    projectionVariables,
                    this.variableName,
                    this.variableName,
                    SparkSessionManager.expressionHashColumnName,
                    SparkSessionManager.inputTupleHashColumnName
                )
            );

        // We now post-filter on the predicate, by hash group.
        RuntimeIterator filteringPredicateIterator = new PredicateIterator(
                new VariableReferenceIterator(
                        this.variableName,
                        SequenceType.MOST_GENERAL_SEQUENCE_TYPE,
                        ExecutionMode.LOCAL,
                        getMetadata()
                ),
                predicateIterator,
                ExecutionMode.LOCAL,
                getMetadata()
        );
        inputDF = LetClauseSparkIterator.bindLetVariableInDataFrame(
            inputDF,
            this.variableName,
            filteringPredicateIterator,
            context,
            (this.child == null)?Collections.emptyList():new ArrayList<Name>(this.child.getOutputTupleVariableNames()),
            parentProjection,
            false
        );

        return inputDF;
    }

    public static boolean isExpressionIndependentFromInputTuple(
            RuntimeIterator sequenceIterator,
            RuntimeTupleIterator tupleIterator
    ) {
        // Check that the expression does not depend functionally on the input tuples
        Set<Name> intersection = new HashSet<>(
                sequenceIterator.getVariableDependencies().keySet()
        );
        intersection.retainAll(tupleIterator.getOutputTupleVariableNames());
        return intersection.isEmpty();
    }

    public Map<Name, DynamicContext.VariableDependency> getVariableDependencies() {
        Map<Name, DynamicContext.VariableDependency> result =
            new TreeMap<>(this.assignmentIterator.getVariableDependencies());
        if (this.child != null) {
            for (Name var : this.child.getOutputTupleVariableNames()) {
                result.remove(var);
            }
            result.putAll(this.child.getVariableDependencies());
        }
        return result;
    }

    public Set<Name> getOutputTupleVariableNames() {
        Set<Name> result = new HashSet<>();
        if (this.child != null) {
            result.addAll(this.child.getOutputTupleVariableNames());
        }
        result.add(this.variableName);
        return result;
    }

    public void print(StringBuffer buffer, int indent) {
        super.print(buffer, indent);
        for (int i = 0; i < indent + 1; ++i) {
            buffer.append("  ");
        }
        buffer.append("Variable ").append(this.variableName).append("\n");
        this.assignmentIterator.print(buffer, indent + 1);
    }

    public Map<Name, DynamicContext.VariableDependency> getProjection(
            Map<Name, DynamicContext.VariableDependency> parentProjection
    ) {
        if (this.child == null) {
            return null;
        }

        // start with an empty projection.

        // copy over the projection needed by the parent clause.
        Map<Name, DynamicContext.VariableDependency> projection =
            new TreeMap<>(parentProjection);

        // remove the variable that this clause binds.
        projection.remove(this.variableName);

        // add the variable dependencies needed by this for clause's expression.
        Map<Name, DynamicContext.VariableDependency> exprDependency = this.assignmentIterator
            .getVariableDependencies();
        for (Name variable : exprDependency.keySet()) {
            if (projection.containsKey(variable)) {
                if (projection.get(variable) != exprDependency.get(variable)) {
                    if (
                        this.child != null && this.child.getOutputTupleVariableNames().contains(variable)
                    ) {
                        projection.put(variable, DynamicContext.VariableDependency.FULL);
                    }
                }
            } else {
                if (this.child != null && this.child.getOutputTupleVariableNames().contains(variable)) {
                    projection.put(variable, exprDependency.get(variable));
                }
            }
        }
        return projection;
    }

    /**
     * Extends a DataFrame with a new column obtained from the evaluation of an expression for each tuple.
     * 
     * @param df the DataFrame to extend
     * @param newVariableName the name of the new column (variable)
     * @param newVariableExpression the expression to evaluate
     * @param context the context (in addition to each tuple) in which to evaluation the expression
     * @param dependencies the dependencies to project to (possibly null to keep everything).
     * @param hash whether or not to compute single-item hashes rather than the actual serialized sequences of items.
     * @return
     */
    public static Dataset<Row> bindLetVariableInDataFrame(
            Dataset<Row> df,
            Name newVariableName,
            RuntimeIterator newVariableExpression,
            DynamicContext context,
            List<Name> projectedNames,
            Map<Name, DynamicContext.VariableDependency> dependencies,
            boolean hash
    ) {
        StructType inputSchema = df.schema();

        List<String> allColumns = FlworDataFrameUtils.getColumnNames(
            inputSchema,
            dependencies,
            null,
            Collections.singletonList(newVariableName)
        );
        
        List<String> UDFcolumns = FlworDataFrameUtils.getColumnNames(
            inputSchema,
            newVariableExpression.getVariableDependencies(),
            projectedNames,
            null
        );

        if (!hash) {
            df.sparkSession()
                .udf()
                .register(
                    "letClauseUDF",
                    new LetClauseUDF(newVariableExpression, context, inputSchema, UDFcolumns),
                    DataTypes.BinaryType
                );
        } else {
            df.sparkSession()
                .udf()
                .register(
                    "hashUDF",
                    new HashUDF(newVariableExpression, context, inputSchema, UDFcolumns),
                    DataTypes.LongType
                );
        }

        String selectSQL = FlworDataFrameUtils.getSQLProjection(allColumns, true);
        String UDFParameters = FlworDataFrameUtils.getUDFParameters(UDFcolumns);

        df.createOrReplaceTempView("input");
        if (!hash) {
            df = df.sparkSession()
                .sql(
                    String.format(
                        "select %s letClauseUDF(%s) as `%s` from input",
                        selectSQL,
                        UDFParameters,
                        newVariableName
                    )
                );
        } else {
            df = df.sparkSession()
                .sql(
                    String.format(
                        "select %s hashUDF(%s) as `%s` from input",
                        selectSQL,
                        UDFParameters,
                        newVariableName
                    )
                );
        }
        return df;
    }
}
