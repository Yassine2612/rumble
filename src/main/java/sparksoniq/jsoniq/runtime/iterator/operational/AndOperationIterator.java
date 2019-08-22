/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package sparksoniq.jsoniq.runtime.iterator.operational;

import sparksoniq.exceptions.IteratorFlowException;
import sparksoniq.jsoniq.compiler.translator.expr.operational.base.OperationalExpressionBase;
import sparksoniq.jsoniq.item.Item;
import sparksoniq.jsoniq.item.ItemFactory;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.jsoniq.runtime.iterator.operational.base.BinaryOperationBaseIterator;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;

public class AndOperationIterator extends BinaryOperationBaseIterator {

	private static final long serialVersionUID = 1L;

    public AndOperationIterator(RuntimeIterator left, RuntimeIterator right, IteratorMetadata iteratorMetadata) {
        super(left, right, OperationalExpressionBase.Operator.AND, iteratorMetadata);
    }

    @Override
    public Item next() {
        if (this._hasNext) {
            _leftIterator.open(_currentDynamicContext);
            _rightIterator.open(_currentDynamicContext);

            boolean leftEffectiveBooleanValue = getEffectiveBooleanValue(_leftIterator);
            boolean rightEffectiveBooleanValue = getEffectiveBooleanValue(_rightIterator);

            _leftIterator.close();
            _rightIterator.close();
            this._hasNext = false;
            return ItemFactory.getInstance().createBooleanItem((leftEffectiveBooleanValue && rightEffectiveBooleanValue));
        }
        throw new IteratorFlowException(RuntimeIterator.FLOW_EXCEPTION_MESSAGE, getMetadata());

    }
}
