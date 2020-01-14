package sparksoniq.spark.ml;

import sparksoniq.exceptions.UnknownRumbleMLClassReferenceException;
import sparksoniq.exceptions.UnknownRumbleMLParamReferenceException;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SparkMLCatalog {
    private static final HashMap<String, String> estimatorFullClassNames;
    private static final HashMap<String, String> transformerFullClassNames;

    private static final HashMap<String, List<String>> estimatorParams;
    private static final HashMap<String, List<String>> transformerParams;

    static {
        estimatorFullClassNames = new HashMap<>();
        transformerFullClassNames = new HashMap<>();
        estimatorParams = new HashMap<>();
        transformerParams = new HashMap<>();

        estimatorFullClassNames.put("RandomForestRegressor", "org.apache.spark.ml.regression.RandomForestRegressor");
        estimatorFullClassNames.put("ALS", "org.apache.spark.ml.recommendation.ALS");
        estimatorFullClassNames.put("FPGrowth", "org.apache.spark.ml.fpm.FPGrowth");
        estimatorFullClassNames.put("TrainValidationSplit", "org.apache.spark.ml.tuning.TrainValidationSplit");
        estimatorFullClassNames.put("CrossValidator", "org.apache.spark.ml.tuning.CrossValidator");
        estimatorFullClassNames.put("LinearRegression", "org.apache.spark.ml.regression.LinearRegression");
        estimatorFullClassNames.put("IsotonicRegression", "org.apache.spark.ml.regression.IsotonicRegression");
        estimatorFullClassNames.put("GeneralizedLinearRegression", "org.apache.spark.ml.regression.GeneralizedLinearRegression");
        estimatorFullClassNames.put("GBTRegressor", "org.apache.spark.ml.regression.GBTRegressor");
        estimatorFullClassNames.put("DecisionTreeRegressor", "org.apache.spark.ml.regression.DecisionTreeRegressor");
        estimatorFullClassNames.put("AFTSurvivalRegression", "org.apache.spark.ml.regression.AFTSurvivalRegression");
        estimatorFullClassNames.put("KMeans", "org.apache.spark.ml.clustering.KMeans");
        estimatorFullClassNames.put("LDA", "org.apache.spark.ml.clustering.LDA");
        estimatorFullClassNames.put("GaussianMixture", "org.apache.spark.ml.clustering.GaussianMixture");
        estimatorFullClassNames.put("Pipeline", "org.apache.spark.ml.Pipeline");
        estimatorFullClassNames.put("RandomForestClassifier", "org.apache.spark.ml.classification.RandomForestClassifier");
        estimatorFullClassNames.put("OneVsRest", "org.apache.spark.ml.classification.OneVsRest");
        estimatorFullClassNames.put("NaiveBayes", "org.apache.spark.ml.classification.NaiveBayes");
        estimatorFullClassNames.put("MultilayerPerceptronClassifier", "org.apache.spark.ml.classification.MultilayerPerceptronClassifier");
        estimatorFullClassNames.put("LinearSVC", "org.apache.spark.ml.classification.LinearSVC");
        estimatorFullClassNames.put("GBTClassifier", "org.apache.spark.ml.classification.GBTClassifier");
        estimatorFullClassNames.put("DecisionTreeClassifier", "org.apache.spark.ml.classification.DecisionTreeClassifier");
        estimatorFullClassNames.put("LogisticRegression", "org.apache.spark.ml.classification.LogisticRegression");
        estimatorFullClassNames.put("Word2Vec", "org.apache.spark.ml.feature.Word2Vec");
        estimatorFullClassNames.put("VectorIndexer", "org.apache.spark.ml.feature.VectorIndexer");
        estimatorFullClassNames.put("StringIndexer", "org.apache.spark.ml.feature.StringIndexer");
        estimatorFullClassNames.put("StandardScaler", "org.apache.spark.ml.feature.StandardScaler");
        estimatorFullClassNames.put("RFormula", "org.apache.spark.ml.feature.RFormula");
        estimatorFullClassNames.put("QuantileDiscretizer", "org.apache.spark.ml.feature.QuantileDiscretizer");
        estimatorFullClassNames.put("BisectingKMeans", "org.apache.spark.ml.clustering.BisectingKMeans");
        estimatorFullClassNames.put("PCA", "org.apache.spark.ml.feature.PCA");
        estimatorFullClassNames.put("OneHotEncoderEstimator", "org.apache.spark.ml.feature.OneHotEncoderEstimator");
        estimatorFullClassNames.put("MinMaxScaler", "org.apache.spark.ml.feature.MinMaxScaler");
        estimatorFullClassNames.put("MinHashLSH", "org.apache.spark.ml.feature.MinHashLSH");
        estimatorFullClassNames.put("MaxAbsScaler", "org.apache.spark.ml.feature.MaxAbsScaler");
        estimatorFullClassNames.put("Imputer", "org.apache.spark.ml.feature.Imputer");
        estimatorFullClassNames.put("IDF", "org.apache.spark.ml.feature.IDF");
        estimatorFullClassNames.put("CountVectorizer", "org.apache.spark.ml.feature.CountVectorizer");
        estimatorFullClassNames.put("ChiSqSelector", "org.apache.spark.ml.feature.ChiSqSelector");
        estimatorFullClassNames.put("BucketedRandomProjectionLSH", "org.apache.spark.ml.feature.BucketedRandomProjectionLSH");

        transformerFullClassNames.put("ALSModel", "org.apache.spark.ml.recommendation.ALSModel");
        transformerFullClassNames.put("RandomForestRegressionModel", "org.apache.spark.ml.regression.RandomForestRegressionModel");
        transformerFullClassNames.put("FPGrowthModel", "org.apache.spark.ml.fpm.FPGrowthModel");
        transformerFullClassNames.put("TrainValidationSplitModel", "org.apache.spark.ml.tuning.TrainValidationSplitModel");
        transformerFullClassNames.put("CrossValidatorModel", "org.apache.spark.ml.tuning.CrossValidatorModel");
        transformerFullClassNames.put("IsotonicRegressionModel", "org.apache.spark.ml.regression.IsotonicRegressionModel");
        transformerFullClassNames.put("LinearRegressionModel", "org.apache.spark.ml.regression.LinearRegressionModel");
        transformerFullClassNames.put("GeneralizedLinearRegressionModel", "org.apache.spark.ml.regression.GeneralizedLinearRegressionModel");
        transformerFullClassNames.put("GBTRegressionModel", "org.apache.spark.ml.regression.GBTRegressionModel");
        transformerFullClassNames.put("AFTSurvivalRegressionModel", "org.apache.spark.ml.regression.AFTSurvivalRegressionModel");
        transformerFullClassNames.put("DecisionTreeRegressionModel", "org.apache.spark.ml.regression.DecisionTreeRegressionModel");
        transformerFullClassNames.put("KMeansModel", "org.apache.spark.ml.clustering.KMeansModel");
        transformerFullClassNames.put("LocalLDAModel", "org.apache.spark.ml.clustering.LocalLDAModel");
        transformerFullClassNames.put("GaussianMixtureModel", "org.apache.spark.ml.clustering.GaussianMixtureModel");
        transformerFullClassNames.put("DistributedLDAModel", "org.apache.spark.ml.clustering.DistributedLDAModel");
        transformerFullClassNames.put("PipelineModel", "org.apache.spark.ml.PipelineModel");
        transformerFullClassNames.put("RandomForestClassificationModel", "org.apache.spark.ml.classification.RandomForestClassificationModel");
        transformerFullClassNames.put("OneVsRestModel", "org.apache.spark.ml.classification.OneVsRestModel");
        transformerFullClassNames.put("NaiveBayesModel", "org.apache.spark.ml.classification.NaiveBayesModel");
        transformerFullClassNames.put("MultilayerPerceptronClassificationModel", "org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel");
        transformerFullClassNames.put("LogisticRegressionModel", "org.apache.spark.ml.classification.LogisticRegressionModel");
        transformerFullClassNames.put("LinearSVCModel", "org.apache.spark.ml.classification.LinearSVCModel");
        transformerFullClassNames.put("GBTClassificationModel", "org.apache.spark.ml.classification.GBTClassificationModel");
        transformerFullClassNames.put("DecisionTreeClassificationModel", "org.apache.spark.ml.classification.DecisionTreeClassificationModel");
        transformerFullClassNames.put("Word2VecModel", "org.apache.spark.ml.feature.Word2VecModel");
        transformerFullClassNames.put("VectorSlicer", "org.apache.spark.ml.feature.VectorSlicer");
        transformerFullClassNames.put("VectorSizeHint", "org.apache.spark.ml.feature.VectorSizeHint");
        transformerFullClassNames.put("VectorIndexerModel", "org.apache.spark.ml.feature.VectorIndexerModel");
        transformerFullClassNames.put("VectorAttributeRewriter", "org.apache.spark.ml.feature.VectorAttributeRewriter");
        transformerFullClassNames.put("VectorAssembler", "org.apache.spark.ml.feature.VectorAssembler");
        transformerFullClassNames.put("Tokenizer", "org.apache.spark.ml.feature.Tokenizer");
        transformerFullClassNames.put("StringIndexerModel", "org.apache.spark.ml.feature.StringIndexerModel");
        transformerFullClassNames.put("StopWordsRemover", "org.apache.spark.ml.feature.StopWordsRemover");
        transformerFullClassNames.put("StandardScalerModel", "org.apache.spark.ml.feature.StandardScalerModel");
        transformerFullClassNames.put("SQLTransformer", "org.apache.spark.ml.feature.SQLTransformer");
        transformerFullClassNames.put("RFormulaModel", "org.apache.spark.ml.feature.RFormulaModel");
        transformerFullClassNames.put("RegexTokenizer", "org.apache.spark.ml.feature.RegexTokenizer");
        transformerFullClassNames.put("PolynomialExpansion", "org.apache.spark.ml.feature.PolynomialExpansion");
        transformerFullClassNames.put("PCAModel", "org.apache.spark.ml.feature.PCAModel");
        transformerFullClassNames.put("BisectingKMeansModel", "org.apache.spark.ml.clustering.BisectingKMeansModel");
        transformerFullClassNames.put("OneHotEncoderModel", "org.apache.spark.ml.feature.OneHotEncoderModel");
        transformerFullClassNames.put("OneHotEncoder", "org.apache.spark.ml.feature.OneHotEncoder");
        transformerFullClassNames.put("Normalizer", "org.apache.spark.ml.feature.Normalizer");
        transformerFullClassNames.put("NGram", "org.apache.spark.ml.feature.NGram");
        transformerFullClassNames.put("MinMaxScalerModel", "org.apache.spark.ml.feature.MinMaxScalerModel");
        transformerFullClassNames.put("MinHashLSHModel", "org.apache.spark.ml.feature.MinHashLSHModel");
        transformerFullClassNames.put("MaxAbsScalerModel", "org.apache.spark.ml.feature.MaxAbsScalerModel");
        transformerFullClassNames.put("Interaction", "org.apache.spark.ml.feature.Interaction");
        transformerFullClassNames.put("IndexToString", "org.apache.spark.ml.feature.IndexToString");
        transformerFullClassNames.put("ImputerModel", "org.apache.spark.ml.feature.ImputerModel");
        transformerFullClassNames.put("IDFModel", "org.apache.spark.ml.feature.IDFModel");
        transformerFullClassNames.put("HashingTF", "org.apache.spark.ml.feature.HashingTF");
        transformerFullClassNames.put("FeatureHasher", "org.apache.spark.ml.feature.FeatureHasher");
        transformerFullClassNames.put("ElementwiseProduct", "org.apache.spark.ml.feature.ElementwiseProduct");
        transformerFullClassNames.put("DCT", "org.apache.spark.ml.feature.DCT");
        transformerFullClassNames.put("CountVectorizerModel", "org.apache.spark.ml.feature.CountVectorizerModel");
        transformerFullClassNames.put("ColumnPruner", "org.apache.spark.ml.feature.ColumnPruner");
        transformerFullClassNames.put("ChiSqSelectorModel", "org.apache.spark.ml.feature.ChiSqSelectorModel");
        transformerFullClassNames.put("Bucketizer", "org.apache.spark.ml.feature.Bucketizer");
        transformerFullClassNames.put("BucketedRandomProjectionLSHModel", "org.apache.spark.ml.feature.BucketedRandomProjectionLSHModel");
        transformerFullClassNames.put("Binarizer", "org.apache.spark.ml.feature.Binarizer");

        estimatorParams.put("RandomForestRegressor", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "LabelCol", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "NumTrees", "PredictionCol", "Seed", "SubsamplingRate")));
        estimatorParams.put("ALS", new ArrayList<>(Arrays.asList("Alpha", "CheckpointInterval", "ColdStartStrategy", "FinalStorageLevel", "ImplicitPrefs", "IntermediateStorageLevel", "ItemCol", "MaxIter", "Nonnegative", "NumBlocks", "NumItemBlocks", "NumUserBlocks", "PredictionCol", "Rank", "RatingCol", "RegParam", "Seed", "UserCol")));
        estimatorParams.put("FPGrowth", new ArrayList<>(Arrays.asList("ItemsCol", "MinConfidence", "MinSupport", "NumPartitions", "PredictionCol")));
        estimatorParams.put("TrainValidationSplit", new ArrayList<>(Arrays.asList("CollectSubModels", "Estimator", "EstimatorParamMaps", "Evaluator", "Parallelism", "Seed", "TrainRatio")));
        estimatorParams.put("CrossValidator", new ArrayList<>(Arrays.asList("CollectSubModels", "Estimator", "EstimatorParamMaps", "Evaluator", "NumFolds", "Parallelism", "Seed")));
        estimatorParams.put("LinearRegression", new ArrayList<>(Arrays.asList("AggregationDepth", "ElasticNetParam", "Epsilon", "FeaturesCol", "FitIntercept", "LabelCol", "Loss", "MaxIter", "PredictionCol", "RegParam", "Solver", "Standardization", "Tol", "WeightCol")));
        estimatorParams.put("IsotonicRegression", new ArrayList<>(Arrays.asList("FeatureIndex", "FeaturesCol", "Isotonic", "LabelCol", "PredictionCol", "WeightCol")));
        estimatorParams.put("GeneralizedLinearRegression", new ArrayList<>(Arrays.asList("Family", "FeaturesCol", "FitIntercept", "LabelCol", "Link", "LinkPower", "LinkPredictionCol", "MaxIter", "OffsetCol", "PredictionCol", "RegParam", "Solver", "Tol", "VariancePower", "WeightCol")));
        estimatorParams.put("GBTRegressor", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "LabelCol", "LossType", "MaxBins", "MaxDepth", "MaxIter", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "PredictionCol", "Seed", "StepSize", "SubsamplingRate", "ValidationIndicatorCol")));
        estimatorParams.put("DecisionTreeRegressor", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "Impurity", "LabelCol", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "PredictionCol", "Seed", "VarianceCol")));
        estimatorParams.put("AFTSurvivalRegression", new ArrayList<>(Arrays.asList("AggregationDepth", "CensorCol", "FeaturesCol", "FitIntercept", "LabelCol", "MaxIter", "PredictionCol", "QuantileProbabilities", "QuantilesCol", "Tol")));
        estimatorParams.put("KMeans", new ArrayList<>(Arrays.asList("DistanceMeasure", "FeaturesCol", "InitMode", "InitSteps", "K", "MaxIter", "PredictionCol", "Seed", "Tol")));
        estimatorParams.put("LDA", new ArrayList<>(Arrays.asList("CheckpointInterval", "DocConcentration", "DocConcentration", "FeaturesCol", "K", "KeepLastCheckpoint", "LearningDecay", "LearningOffset", "MaxIter", "OptimizeDocConcentration", "Optimizer", "Seed", "SubsamplingRate", "TopicConcentration", "TopicDistributionCol")));
        estimatorParams.put("GaussianMixture", new ArrayList<>(Arrays.asList("FeaturesCol", "K", "MaxIter", "PredictionCol", "ProbabilityCol", "Seed", "Tol")));
        estimatorParams.put("Pipeline", new ArrayList<>(Arrays.asList("Stages")));
        estimatorParams.put("RandomForestClassifier", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "LabelCol", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "NumTrees", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "SubsamplingRate", "Thresholds")));
        estimatorParams.put("OneVsRest", new ArrayList<>(Arrays.asList("Classifier", "FeaturesCol", "LabelCol", "Parallelism", "PredictionCol", "RawPredictionCol", "WeightCol")));
        estimatorParams.put("NaiveBayes", new ArrayList<>(Arrays.asList("FeaturesCol", "LabelCol", "ModelType", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Smoothing", "Thresholds", "WeightCol")));
        estimatorParams.put("MultilayerPerceptronClassifier", new ArrayList<>(Arrays.asList("BlockSize", "FeaturesCol", "InitialWeights", "LabelCol", "Layers", "MaxIter", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "Solver", "StepSize", "Thresholds", "Tol")));
        estimatorParams.put("LinearSVC", new ArrayList<>(Arrays.asList("AggregationDepth", "FeaturesCol", "FitIntercept", "LabelCol", "MaxIter", "PredictionCol", "RawPredictionCol", "RegParam", "Standardization", "Threshold", "Tol", "WeightCol")));
        estimatorParams.put("GBTClassifier", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "LabelCol", "LossType", "MaxBins", "MaxDepth", "MaxIter", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "StepSize", "SubsamplingRate", "Thresholds", "ValidationIndicatorCol")));
        estimatorParams.put("DecisionTreeClassifier", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "Impurity", "LabelCol", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "Thresholds")));
        estimatorParams.put("LogisticRegression", new ArrayList<>(Arrays.asList("AggregationDepth", "ElasticNetParam", "Family", "FeaturesCol", "FitIntercept", "LabelCol", "LowerBoundsOnCoefficients", "LowerBoundsOnIntercepts", "MaxIter", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "RegParam", "Standardization", "Threshold", "Thresholds", "Tol", "UpperBoundsOnCoefficients", "UpperBoundsOnIntercepts", "WeightCol")));
        estimatorParams.put("Word2Vec", new ArrayList<>(Arrays.asList("InputCol", "MaxIter", "MaxSentenceLength", "MinCount", "NumPartitions", "OutputCol", "Seed", "StepSize", "VectorSize", "WindowSize")));
        estimatorParams.put("VectorIndexer", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "MaxCategories", "OutputCol")));
        estimatorParams.put("StringIndexer", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "OutputCol", "StringOrderType")));
        estimatorParams.put("StandardScaler", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "WithMean", "WithStd")));
        estimatorParams.put("RFormula", new ArrayList<>(Arrays.asList("FeaturesCol", "ForceIndexLabel", "Formula", "HandleInvalid", "LabelCol", "StringIndexerOrderType")));
        estimatorParams.put("QuantileDiscretizer", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "InputCols", "NumBuckets", "NumBucketsArray", "OutputCol", "OutputCols", "RelativeError")));
        estimatorParams.put("BisectingKMeans", new ArrayList<>(Arrays.asList("DistanceMeasure", "FeaturesCol", "K", "MaxIter", "MinDivisibleClusterSize", "PredictionCol", "Seed")));
        estimatorParams.put("PCA", new ArrayList<>(Arrays.asList("InputCol", "K", "OutputCol")));
        estimatorParams.put("OneHotEncoderEstimator", new ArrayList<>(Arrays.asList("DropLast", "HandleInvalid", "InputCols", "OutputCols")));
        estimatorParams.put("MinMaxScaler", new ArrayList<>(Arrays.asList("InputCol", "Max", "Min", "OutputCol")));
        estimatorParams.put("MinHashLSH", new ArrayList<>(Arrays.asList("InputCol", "NumHashTables", "OutputCol", "Seed")));
        estimatorParams.put("MaxAbsScaler", new ArrayList<>(Arrays.asList("InputCol", "OutputCol")));
        estimatorParams.put("Imputer", new ArrayList<>(Arrays.asList("InputCols", "MissingValue", "OutputCols", "Strategy")));
        estimatorParams.put("IDF", new ArrayList<>(Arrays.asList("InputCol", "MinDocFreq", "OutputCol")));
        estimatorParams.put("CountVectorizer", new ArrayList<>(Arrays.asList("Binary", "InputCol", "MaxDF", "MinDF", "MinTF", "OutputCol", "VocabSize")));
        estimatorParams.put("ChiSqSelector", new ArrayList<>(Arrays.asList("Fdr", "FeaturesCol", "Fpr", "Fwe", "LabelCol", "NumTopFeatures", "OutputCol", "Percentile", "SelectorType")));
        estimatorParams.put("BucketedRandomProjectionLSH", new ArrayList<>(Arrays.asList("BucketLength", "InputCol", "NumHashTables", "OutputCol", "Seed")));

        transformerParams.put("ALSModel", new ArrayList<>(Arrays.asList("ColdStartStrategy", "ItemCol", "Parent", "PredictionCol", "UserCol")));
        transformerParams.put("RandomForestRegressionModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "NumTrees", "Parent", "PredictionCol", "Seed", "SubsamplingRate")));
        transformerParams.put("FPGrowthModel", new ArrayList<>(Arrays.asList("ItemsCol", "MinConfidence", "Parent", "PredictionCol")));
        transformerParams.put("TrainValidationSplitModel", new ArrayList<>(Arrays.asList("Parent")));
        transformerParams.put("CrossValidatorModel", new ArrayList<>(Arrays.asList("Parent")));
        transformerParams.put("IsotonicRegressionModel", new ArrayList<>(Arrays.asList("FeatureIndex", "FeaturesCol", "Parent", "PredictionCol")));
        transformerParams.put("LinearRegressionModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol")));
        transformerParams.put("GeneralizedLinearRegressionModel", new ArrayList<>(Arrays.asList("FeaturesCol", "LinkPredictionCol", "Parent", "PredictionCol")));
        transformerParams.put("GBTRegressionModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "MaxBins", "MaxDepth", "MaxIter", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "Parent", "PredictionCol", "Seed", "StepSize", "SubsamplingRate")));
        transformerParams.put("AFTSurvivalRegressionModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "QuantileProbabilities", "QuantilesCol")));
        transformerParams.put("DecisionTreeRegressionModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "Impurity", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "Parent", "PredictionCol", "Seed", "VarianceCol")));
        transformerParams.put("KMeansModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol")));
        transformerParams.put("LocalLDAModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "Seed", "TopicDistributionCol")));
        transformerParams.put("GaussianMixtureModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "ProbabilityCol")));
        transformerParams.put("DistributedLDAModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "Seed", "TopicDistributionCol")));
        transformerParams.put("PipelineModel", new ArrayList<>(Arrays.asList("Parent")));
        transformerParams.put("RandomForestClassificationModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "NumTrees", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "SubsamplingRate", "Thresholds")));
        transformerParams.put("OneVsRestModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "RawPredictionCol")));
        transformerParams.put("NaiveBayesModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Thresholds")));
        transformerParams.put("MultilayerPerceptronClassificationModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Thresholds")));
        transformerParams.put("LogisticRegressionModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Threshold", "Thresholds")));
        transformerParams.put("LinearSVCModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol", "RawPredictionCol", "Threshold", "WeightCol")));
        transformerParams.put("GBTClassificationModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "FeatureSubsetStrategy", "Impurity", "MaxBins", "MaxDepth", "MaxIter", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "StepSize", "SubsamplingRate", "Thresholds")));
        transformerParams.put("DecisionTreeClassificationModel", new ArrayList<>(Arrays.asList("CacheNodeIds", "CheckpointInterval", "FeaturesCol", "Impurity", "MaxBins", "MaxDepth", "MaxMemoryInMB", "MinInfoGain", "MinInstancesPerNode", "Parent", "PredictionCol", "ProbabilityCol", "RawPredictionCol", "Seed", "Thresholds")));
        transformerParams.put("Word2VecModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("VectorSlicer", new ArrayList<>(Arrays.asList("Indices", "InputCol", "Names", "OutputCol")));
        transformerParams.put("VectorSizeHint", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "Size")));
        transformerParams.put("VectorIndexerModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("VectorAttributeRewriter", new ArrayList<>(Arrays.asList()));
        transformerParams.put("VectorAssembler", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCols", "OutputCol")));
        transformerParams.put("Tokenizer", new ArrayList<>(Arrays.asList("InputCol", "OutputCol")));
        transformerParams.put("StringIndexerModel", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "OutputCol", "Parent")));
        transformerParams.put("StopWordsRemover", new ArrayList<>(Arrays.asList("CaseSensitive", "InputCol", "Locale", "OutputCol", "StopWords")));
        transformerParams.put("StandardScalerModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("SQLTransformer", new ArrayList<>(Arrays.asList("Statement")));
        transformerParams.put("RFormulaModel", new ArrayList<>(Arrays.asList("Parent")));
        transformerParams.put("RegexTokenizer", new ArrayList<>(Arrays.asList("Gaps", "InputCol", "MinTokenLength", "OutputCol", "Pattern", "ToLowercase")));
        transformerParams.put("PolynomialExpansion", new ArrayList<>(Arrays.asList("Degree", "InputCol", "OutputCol")));
        transformerParams.put("PCAModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("BisectingKMeansModel", new ArrayList<>(Arrays.asList("FeaturesCol", "Parent", "PredictionCol")));
        transformerParams.put("OneHotEncoderModel", new ArrayList<>(Arrays.asList("DropLast", "HandleInvalid", "InputCols", "OutputCols", "Parent")));
        transformerParams.put("OneHotEncoder", new ArrayList<>(Arrays.asList("DropLast", "InputCol", "OutputCol")));
        transformerParams.put("Normalizer", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "P")));
        transformerParams.put("NGram", new ArrayList<>(Arrays.asList("InputCol", "N", "OutputCol")));
        transformerParams.put("MinMaxScalerModel", new ArrayList<>(Arrays.asList("InputCol", "Max", "Min", "OutputCol", "Parent")));
        transformerParams.put("MinHashLSHModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("MaxAbsScalerModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("Interaction", new ArrayList<>(Arrays.asList("InputCols", "OutputCol")));
        transformerParams.put("IndexToString", new ArrayList<>(Arrays.asList("InputCol", "Labels", "OutputCol")));
        transformerParams.put("ImputerModel", new ArrayList<>(Arrays.asList("InputCols", "OutputCols", "Parent")));
        transformerParams.put("IDFModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("HashingTF", new ArrayList<>(Arrays.asList("Binary", "InputCol", "NumFeatures", "OutputCol")));
        transformerParams.put("FeatureHasher", new ArrayList<>(Arrays.asList("CategoricalCols", "InputCols", "InputCols", "NumFeatures", "OutputCol")));
        transformerParams.put("ElementwiseProduct", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "ScalingVec")));
        transformerParams.put("DCT", new ArrayList<>(Arrays.asList("InputCol", "Inverse", "OutputCol")));
        transformerParams.put("CountVectorizerModel", new ArrayList<>(Arrays.asList("Binary", "InputCol", "MinTF", "OutputCol", "Parent")));
        transformerParams.put("ColumnPruner", new ArrayList<>(Arrays.asList()));
        transformerParams.put("ChiSqSelectorModel", new ArrayList<>(Arrays.asList("FeaturesCol", "OutputCol", "Parent")));
        transformerParams.put("Bucketizer", new ArrayList<>(Arrays.asList("HandleInvalid", "InputCol", "InputCols", "OutputCol", "OutputCols", "Parent", "Splits", "SplitsArray")));
        transformerParams.put("BucketedRandomProjectionLSHModel", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Parent")));
        transformerParams.put("Binarizer", new ArrayList<>(Arrays.asList("InputCol", "OutputCol", "Threshold")));

    }

    public static String getEstimatorFullClassName(String name, IteratorMetadata metadata) {
        if (!estimatorFullClassNames.containsKey(name)) {
            throw new UnknownRumbleMLClassReferenceException(name, metadata);
        }
        return estimatorFullClassNames.get(name);
    }

    public static String getTransformerFullClassName(String name, IteratorMetadata metadata) {
        if (!transformerFullClassNames.containsKey(name)) {
            throw new UnknownRumbleMLClassReferenceException(name, metadata);
        }
        return transformerFullClassNames.get(name);
    }

    public static List<String> getEstimatorParams(String name, IteratorMetadata metadata) {
        if (!estimatorParams.containsKey(name)) {
            throw new UnknownRumbleMLClassReferenceException(name, metadata);
        }
        return estimatorParams.get(name);
    }

    public static List<String> getTransformerParams(String name, IteratorMetadata metadata) {
        if (!transformerParams.containsKey(name)) {
            throw new UnknownRumbleMLClassReferenceException(name, metadata);
        }
        return transformerParams.get(name);
    }

    public static void validateParameterForTransformer(String transformerName, String paramName, IteratorMetadata metadata) {
        if (!transformerParams.containsKey(transformerName)) {
            throw new UnknownRumbleMLClassReferenceException(transformerName, metadata);
        }
        if (!transformerParams.get(transformerName).contains(paramName)) {
            throw new UnknownRumbleMLParamReferenceException(paramName, transformerName, metadata);
        }
    }
}
