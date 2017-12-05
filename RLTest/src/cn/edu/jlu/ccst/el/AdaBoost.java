package cn.edu.jlu.ccst.el;

import java.util.*;

/**
 *
 * @author 瀛旂箒瀹�  MF1333020
 */

public class AdaBoost extends Classifier {
    // 瀛樺偍鍙斁鍥炴娊鍙栫敓鎴愮殑鏂版牱鏈泦
    class RepickSamples {
        double[][] features;
        double[] labels;
        int[] index;
    }
    
    // 瀛︿範缁撴灉
    class LearningResult {
        double error; // 璁粌閿欒鐜�
        boolean correct[];
    }
    
    private static int classifier = 9; // 鐢熸垚鍒嗙被鍣ㄧ殑鏁伴噺
    private Classifier classifiers[];
    private double weightsOfClassifiers[];
    private double weightsOfSamples[];
    
    public AdaBoost() {
    }

    @Override
    public void train(boolean[] isCategory, double[][] features, double[] labels) {
        classifiers = new Classifier[classifier];
        weightsOfClassifiers = new double[classifier];
        
        int samplesCount = labels.length;
        weightsOfSamples = new double[samplesCount];
        
        for (int i = 0; i < samplesCount; ++i) {
            weightsOfSamples[i] = (double)1 / samplesCount;
        }
        
        for (int i = 0; i < classifier; ++i) {
            LearningResult result = null;
            RepickSamples samples = null;
            do {
                samples = repickSamplesWithWeights(features, labels);
                classifiers[i] = new DecisionTree();
                classifiers[i].train(isCategory, samples.features, samples.labels);
                
                result = validateLearningPerformance(classifiers[i], samples);
            } while (result.error > 0.5);
            
            // 璋冩暣鏍锋湰鏉冮噸
            weightsOfClassifiers[i] = 0.5 * Math.log((1 - result.error) / result.error);
            HashSet<Integer> checker = new HashSet<Integer>();
            for (int j = 0; j < result.correct.length; ++j) {
                //鍚屼竴鏍锋湰涓嶉噸澶嶆洿鏂版潈鍊�
                if (checker.contains(samples.index[j])) continue;
                checker.add(samples.index[j]);
                
                if (result.correct[j]) {
                    weightsOfSamples[samples.index[j]] *= Math.exp(-weightsOfClassifiers[i]);
                } else {
                    weightsOfSamples[samples.index[j]] *= Math.exp(weightsOfClassifiers[i]);
                }
            }
            
            // 瑙勮寖鍖�
            double total = 0;
            for (int j = 0; j < samplesCount; ++j) {
                total += weightsOfSamples[j];
            }
            for (int j = 0; j < samplesCount; ++j) {
                weightsOfSamples[j] /= total;
            }
        }
    }
    
    private RepickSamples repickSamplesWithWeights(double[][] features, double[] labels) {
        RepickSamples samples = new RepickSamples();
        int size = labels.length;
        samples.features = new double[size][];
        samples.index = new int[size];
        samples.labels = new double[size];
        
        Random random = new Random();
        for (int i = 0; i < size; ++i) {
            double weight = random.nextDouble();
            double temp = 0;
            int j;
            for (j = 0; j < size; ++j) {
                temp += weightsOfSamples[j];
                if (temp > weight) break;
            }
            if (j == size) j--;
            
            samples.features[i] = features[j].clone();
            samples.labels[i] = labels[j];
            samples.index[i] = j;
        }
        
        return samples;
    }
    
    private LearningResult validateLearningPerformance(Classifier classifier, RepickSamples samples) {
        LearningResult result = new LearningResult();
        result.error = 0;
        result.correct = new boolean[samples.labels.length];
        
        HashSet<Integer> checker = new HashSet<Integer>();
        for (int i = 0; i < samples.labels.length; ++i) {
            if (samples.labels[i] == classifier.predict(samples.features[i])) {
                // 棰勬祴姝ｇ‘
                result.correct[i] = true;
            } else {
                result.correct[i] = false;
                if (checker.contains(samples.index[i])) continue;
                checker.add(samples.index[i]);
                result.error += weightsOfSamples[samples.index[i]];
            }
        }
        return result;
    }

    @Override
    public double predict(double[] features) {
        HashMap<Double, Double> counter = new HashMap<Double, Double>();
        for (int i = 0; i < classifiers.length; ++i) {
            double label = classifiers[i].predict(features);
            if (counter.get(label) == null) {
                counter.put(label, weightsOfClassifiers[i]);
            } else {
                double weight = counter.get(label) + weightsOfClassifiers[i];
                counter.put(label, weight);
            }
        }

        double temp_max = 0;
        double label = 0;
        Iterator<Double> iterator = counter.keySet().iterator();
        while (iterator.hasNext()) {
            double key = iterator.next();
            double weight = counter.get(key);
            if (weight > temp_max) {
                temp_max = weight;
                label = key;
            }
        }

        return label;
    }
}

// <<<<---------------------------鍗庝附鐨勫垎鐣岀嚎锛屼笅闈㈡槸鍐崇瓥鏍戠殑瀹炵幇----------------------------->>>>

class DecisionTree extends Classifier {
    // 鍐崇瓥鏍戣妭鐐圭粨鏋�
    class TreeNode {
        int[] set;                // 鏍锋湰涓嬫爣闆嗗悎
        int[] attr_index;         // 鍙敤灞炴�т笅鏍囬泦鍚�
        double label;             // 鏍囩
        int split_attr;           // 璇ヨ妭鐐圭敤浜庡垎鍓茬殑灞炴�т笅鏍�
        double[] split_points;    // 鍒囧壊鐐� 绂绘暎灞炴�т负澶氬�硷紝杩炵画灞炴�у彧鏈変竴涓��
        TreeNode[] childrenNodes; // 瀛愯妭鐐�
    }

    // 瀛樺偍鍒嗗壊淇℃伅
    class SplitData {
        int split_attr;
        double[] split_points;
        int[][] split_sets;       // 鍒嗗壊鍚庢柊鐨勬牱鏈泦鍚堢殑鏁扮粍
    }

    class BundleData {
        double floatValue;        // 瀛樺偍澧炵泭鐜囨垨MSE
        SplitData split_info;
    }

    // 褰撳垎鍓插嚭鐜伴敊璇椂鎶涘嚭姝ゅ紓甯�
    class SplitException extends Exception {
    }

    private boolean _isClassification;
    private double[][] _features;
    private boolean[] _isCategory;
    private double[] _labels;
    private double[] _defaults;

    private TreeNode root;

    public DecisionTree() {
    }

    @Override
    public void train(boolean[] isCategory, double[][] features, double[] labels) {
        _isClassification = isCategory[isCategory.length - 1];
        _features = features;
        _isCategory = isCategory;
        _labels = labels;

        int set[] = new int[_features.length];
        for (int i = 0; i < set.length; ++i) {
            set[i] = i;
        }

        int attr_index[] = new int[_features[0].length];
        for (int i = 0; i < attr_index.length; ++i) {
            attr_index[i] = i;
        }

        // 澶勭悊缂哄け灞炴��
        _defaults = kill_missing_data();

        root = build_decision_tree(set, attr_index);
    }

    private double[] kill_missing_data() {
        int num = _isCategory.length - 1;
        double[] defaults = new double[num];

        for (int i = 0; i < defaults.length; ++i) {
            if (_isCategory[i]) {
                // 绂绘暎灞炴�у彇鏈�澶氱殑鍊�
                HashMap<Double, Integer> counter = new HashMap<Double, Integer>();
                for (int j = 0; j < _features.length; ++j) {
                    double feature = _features[j][i];
                    if (!Double.isNaN(feature)) {
                        if (counter.get(feature) == null) {
                            counter.put(feature, 1);
                        } else {
                            int count = counter.get(feature) + 1;
                            counter.put(feature, count);
                        }
                    }
                }

                int max_time = 0;
                double value = 0;
                Iterator<Double> iterator = counter.keySet().iterator();
                while (iterator.hasNext()) {
                    double key = iterator.next();
                    int count = counter.get(key);
                    if (count > max_time) {
                        max_time = count;
                        value = key;
                    }
                }
                defaults[i] = value;
            } else {
                // 杩炵画灞炴�у彇骞冲潎鍊�
                int count = 0;
                double total = 0;
                for (int j = 0; j < _features.length; ++j) {
                    if (!Double.isNaN(_features[j][i])) {
                        count++;
                        total += _features[j][i];
                    }
                }
                defaults[i] = total / count;
            }
        }

        // 浠ｆ崲
        for (int i = 0; i < _features.length; ++i) {
            for (int j = 0; j < defaults.length; ++j) {
                if (Double.isNaN(_features[i][j])) {
                    _features[i][j] = defaults[j];
                }
            }
        }
        return defaults;
    }

    @Override
    public double predict(double[] features) {
        // 澶勭悊缂哄け灞炴��
        for (int i = 0; i < features.length; ++i) {
            if (Double.isNaN(features[i])) {
                features[i] = _defaults[i];
            }
        }

        return predict_with_decision_tree(features, root);
    }

    private double predict_with_decision_tree(double[] features, TreeNode node) {
        if (node.childrenNodes == null) {
            return node.label;
        }

        double feature = features[node.split_attr];

        if (_isCategory[node.split_attr]) {
            // 绂绘暎灞炴��
            for (int i = 0; i < node.split_points.length; ++i) {
                if (node.split_points[i] == feature) {
                    return predict_with_decision_tree(features, node.childrenNodes[i]);
                }
            }

            return node.label; // 涓嶅瓨鍦ㄧ殑灞炴�у彇鐖惰妭鐐规牱鏈殑鏍囩锛屽噺灏戝彾瀛愮粨鐐�
        } else {
            // 杩炵画灞炴��
            if (feature < node.split_points[0]) {
                return predict_with_decision_tree(features, node.childrenNodes[0]);
            } else {
                return predict_with_decision_tree(features, node.childrenNodes[1]);
            }
        }

    }

    private TreeNode build_decision_tree(int[] set, int[] attr_index) {
        TreeNode node = new TreeNode();
        node.set = set;
        node.attr_index = attr_index;
        node.label = 0;
        node.childrenNodes = null;

        // 閮戒负鍚岀被杩斿洖鐩存帴杩斿洖
        double label = _labels[node.set[0]];
        boolean flag = true;
        for (int i = 0; i < node.set.length; ++i) {
            if (_labels[node.set[i]] != label) {
                flag = false;
                break;
            }
        }
        if (flag) {
            node.label = label;
            return node;
        }

        // 娌℃湁鍙敤灞炴�ф爣璁颁负澶у鏁�(绂绘暎)鎴栧钩鍧囧��(杩炵画)
        if (_isClassification) {
            node.label = most_label(set);
        } else {
            node.label = mean_value(set);
        }
        if (node.attr_index == null || node.attr_index.length == 0) {
            return node;
        }

        // 瀵绘壘鏈�浼樺垏鍓插睘鎬�
        SplitData split_info = attribute_selection(node);
        node.split_attr = split_info.split_attr;
        // 娌℃湁鍙互鍒嗗壊鐨勫睘鎬�
        if (node.split_attr < 0) {
            return node;
        }

        node.split_points = split_info.split_points;

        // 鍘绘帀宸蹭娇鐢ㄧ殑绂绘暎灞炴�э紝杩炵画灞炴�т笉鍋氬垹闄�
        int[] child_attr_index = null;
        if (_isCategory[node.split_attr]) {
            child_attr_index = new int[attr_index.length - 1];
            int t = 0;
            for (int index : attr_index) {
                if (index != node.split_attr) {
                    child_attr_index[t++] = index;
                }
            }
        } else {
            child_attr_index = node.attr_index.clone();
        }

        // 閫掑綊寤虹珛瀛愯妭鐐�
        node.childrenNodes = new TreeNode[split_info.split_sets.length];
        for (int i = 0; i < split_info.split_sets.length; ++i) {
            node.childrenNodes[i] = build_decision_tree(split_info.split_sets[i], child_attr_index);
        }

        return node;
    }

    // 缁欏畾鏍锋湰涓嚭鐜版渶澶氱殑鏍囩
    private double most_label(int[] set) {
        HashMap<Double, Integer> counter = new HashMap<Double, Integer>();
        for (int item : set) {
            double label = _labels[item];
            if (counter.get(label) == null) {
                counter.put(label, 1);
            } else {
                int count = counter.get(label) + 1;
                counter.put(label, count);
            }
        }

        int max_time = 0;
        double label = 0;
        Iterator<Double> iterator = counter.keySet().iterator();
        while (iterator.hasNext()) {
            double key = iterator.next();
            int count = counter.get(key);
            if (count > max_time) {
                max_time = count;
                label = key;
            }
        }
        return label;
    }

    // 缁欏畾鏍锋湰鐨勬爣绛惧钩鍧囧��
    private double mean_value(int[] set) {
        double temp = 0;
        for (int index : set) {
            temp += _labels[index];
        }
        return temp / set.length;
    }

    private SplitData attribute_selection(TreeNode node) {
        SplitData result = new SplitData();
        result.split_attr = -1;

        // 鍓嶅壀鏋�
        double reference_value = _isClassification ? 0.05 : -1;
        if (node.set.length < 7) return result;

        if (_isClassification) {
            for (int attribute : node.attr_index) {
                try {
                    BundleData gain_ratio_info = gain_ratio_use_attribute(node.set, attribute); // 鍒嗗壊閿欒浼氭姏鍑哄垎鍓插紓甯�
                    if (gain_ratio_info.floatValue > reference_value) {
                        reference_value = gain_ratio_info.floatValue;
                        result = gain_ratio_info.split_info;
                    }
                } catch (SplitException ex) { // 鎹曡幏寮傚父锛岀洿鎺ヤ涪寮�
                }
            }
        } else {
            for (int attribute : node.attr_index) {
                try {
                    BundleData mse_info = mse_use_attribute(node.set, attribute);
                    if (reference_value < 0 || mse_info.floatValue < reference_value) {
                        reference_value = mse_info.floatValue;
                        result = mse_info.split_info;
                    }
                } catch (SplitException ex) {
                }
            }
        }
        return result;
    }

    private SplitData split_with_attribute(int[] set, int attribute) throws SplitException {
        SplitData result = new SplitData();
        result.split_attr = attribute;

        if (_isCategory[attribute]) {
            // 绂绘暎灞炴��
            int amount_of_features = 0;
            HashMap<Double, Integer> counter = new HashMap<Double, Integer>();
            HashMap<Double, Integer> index_recorder = new HashMap<Double, Integer>();
            for (int item : set) {
                double feature = _features[item][attribute];
                if (counter.get(feature) == null) {
                    counter.put(feature, 1);
                    index_recorder.put(feature, amount_of_features++);
                } else {
                    int count = counter.get(feature) + 1;
                    counter.put(feature, count);
                }
            }

            // 璁板綍鍒囧壊鐐�
            result.split_points = new double[amount_of_features];
            Iterator<Double> iterator = index_recorder.keySet().iterator();

            while (iterator.hasNext()) {
                double key = iterator.next();
                int value = index_recorder.get(key);
                result.split_points[value] = key;
            }

            result.split_sets = new int[amount_of_features][];
            int[] t_index = new int[amount_of_features];
            for (int i = 0; i < amount_of_features; ++i) t_index[i] = 0;

            for (int item : set) {
                int index = index_recorder.get(_features[item][attribute]);
                if (result.split_sets[index] == null) {
                    result.split_sets[index] = new int[counter.get(_features[item][attribute])];
                }
                result.split_sets[index][t_index[index]++] = item;
            }
        } else {
            // 杩炵画灞炴��
            double[] features = new double[set.length];
            for (int i = 0; i < features.length; ++i) {
                features[i] = _features[set[i]][attribute];
            }
            Arrays.sort(features);

            double reference_value = _isClassification ? 0 : -1;
            double best_split_point = 0;
            result.split_sets = new int[2][];
            for (int i = 0; i < features.length - 1; ++i) {
                if (features[i] == features[i + 1]) continue;
                double split_point = (features[i] + features[i + 1]) / 2;
                int[] sub_set_a = new int[i + 1];
                int[] sub_set_b = new int[set.length - i - 1];

                int a_index = 0;
                int b_index = 0;
                for (int j = 0; j < set.length; ++j) {
                    if (_features[set[j]][attribute] < split_point) {
                        sub_set_a[a_index++] = set[j];
                    } else {
                        sub_set_b[b_index++] = set[j];
                    }
                }

                if (_isClassification) {
                    double temp = gain_ratio_use_numerical_attribute(set, attribute, sub_set_a, sub_set_b);
                    if (temp > reference_value) {
                        reference_value = temp;
                        best_split_point = split_point;
                        result.split_sets[0] = sub_set_a;
                        result.split_sets[1] = sub_set_b;
                    }
                } else {
                    double temp = (sub_set_a.length * mse(sub_set_a) + sub_set_b.length * mse(sub_set_b)) / set.length;
                    if (reference_value < 0 || temp < reference_value) {
                        reference_value = temp;
                        best_split_point = split_point;
                        result.split_sets[0] = sub_set_a;
                        result.split_sets[1] = sub_set_b;
                    }
                }
            }
            // 娌℃湁鍒嗗壊鐐癸紝鎶涘嚭鍒嗗壊寮傚父
            if (result.split_sets[0] == null && result.split_sets[1] == null) throw new SplitException();
            result.split_points = new double[1];
            result.split_points[0] = best_split_point;
        }
        return result;
    }

    // 璁＄畻缁欏畾鏍锋湰闆嗗悎鐨勭喌
    private double entropy(int[] set) {
        HashMap<Double, Integer> counter = new HashMap<Double, Integer>();
        for (int item : set) {
            double label = _labels[item];
            if (counter.get(label) == null) {
                counter.put(label, 1);
            } else {
                int count = counter.get(label) + 1;
                counter.put(label, count);
            }
        }

        double result = 0;
        Iterator<Double> iterator = counter.keySet().iterator();
        while (iterator.hasNext()) {
            int count = counter.get(iterator.next());
            double p = (double) count / set.length;
            result += -p * Math.log(p);
        }

        return result;
    }

    // 澧炵泭鐜� C4.5
    private BundleData gain_ratio_use_attribute(int[] set, int attribute) throws SplitException {
        BundleData result = new BundleData();
        double entropy_before_split = entropy(set);

        double entropy_after_split = 0;
        double split_information = 0;
        result.split_info = split_with_attribute(set, attribute);
        for (int[] sub_set : result.split_info.split_sets) {
            entropy_after_split += (double) sub_set.length / set.length * entropy(sub_set);
            double p = (double) sub_set.length / set.length;
            split_information += -p * Math.log(p);
        }
        result.floatValue = (entropy_before_split - entropy_after_split) / split_information;
        return result;
    }

    private double gain_ratio_use_numerical_attribute(int[] set, int attribute, int[] part_a, int[] part_b) {
        double entropy_before_split = entropy(set);
        double entropy_after_split = (part_a.length * entropy(part_a) + part_b.length * entropy(part_b)) / set.length;

        double split_information = 0;
        double p = (double) part_a.length / set.length;
        split_information += -p * Math.log(p);
        p = (double) part_b.length / set.length;
        split_information += -p * Math.log(p);

        return (entropy_before_split - entropy_after_split) / split_information;
    }

    private double mse(int[] set) {
        double mean = mean_value(set);

        double temp = 0;
        for (int index : set) {
            double t = _labels[index] - mean;
            temp += t * t;
        }
        return temp / set.length;
    }

    private BundleData mse_use_attribute(int[] set, int attribute) throws SplitException {
        BundleData mse_info = new BundleData();
        mse_info.floatValue = 0;
        mse_info.split_info = split_with_attribute(set, attribute);
        for (int[] sub_set : mse_info.split_info.split_sets) {
            mse_info.floatValue += (double) sub_set.length / set.length * mse(sub_set);
        }
        return mse_info;
    }
}