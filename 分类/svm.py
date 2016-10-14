from libsvm.python.svmutil import *
from libsvm.python.svm import *

y, x = svm_read_problem('pcpDistanceTrain.txt')  # 训练数据
prob = svm_problem(y, x)
#param = svm_parameter('-s 1 -t 3 -c 4')
param = svm_parameter('-t 0 -c 4 -b 0')
model = svm_train(prob, param)     # 训练好的模型
svm_save_model('model_file', model)

yt, xt = svm_read_problem('pcpDistanceTest.txt')
p_label, p_acc, p_val = svm_predict(yt, xt, model)
print(p_label)