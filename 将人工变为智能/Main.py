import xgboost
import numpy as np
import pandas as pd
from sklearn import linear_model, model_selection
from sklearn.metrics import explained_variance_score, accuracy_score, precision_score, recall_score, \
    mean_absolute_error, mean_squared_error

#读入数据

if __name__ == '__main__':
    data = pd.read_csv('../Homework/kc_house_data.csv')

#读入数据
features = data.iloc[:,3:].columns.tolist()
target = data.iloc[:,2].name

# #用于计算数据中各个属性与价格的关联度
# correlations = {}
# for f in features:
#     data_temp = data[[f,target]]
#     x1 = data_temp[f].values
#     x2 = data_temp[target].values
#     key = f + ' vs ' + target
#     correlations[key] = pearsonr(x1,x2)[0]
# data_correlations = pd.DataFrame(correlations, index=['Value']).T
# data_correlations.loc[data_correlations['Value'].abs().sort_values(ascending=False).index]
# print(data_correlations)

#选择前十个与价格关联度最高的属性，并将其作为新的数据集，并以此划分训练集和测试集
regr = linear_model.LinearRegression()
new_data = data[['sqft_living','grade', 'sqft_above', 'sqft_living15','bathrooms','view','sqft_basement','bedrooms','lat','waterfront']]
X = new_data.values
y = data.price.values
X_train, X_test, y_train, y_test = model_selection.train_test_split(X, y ,test_size=0.2)

#训练模型
#线性递归模型
regr = linear_model.LinearRegression()
regr.fit(X_train, y_train)
#xgboost
xgb = xgboost.XGBRegressor(n_estimators=100, learning_rate=0.08, gamma=0, subsample=0.75,
                           colsample_bytree=1, max_depth=7)
xgb.fit(X_train,y_train)

#进行预测
predictions_linear = regr.predict(X_test)
res = np.transpose(predictions_linear)

predictions_xgboost = xgb.predict(X_test)
res1 = np.transpose(predictions_xgboost)

#将预测结果写入result.csv
dataframe = pd.DataFrame({'prediction':res})
dataframe.to_csv("result_linear.csv")

dataframe1 = pd.DataFrame({'prediction':res1})
dataframe.to_csv("result_xgboost.csv")

#评估模型
print(f"线性模型方差得分：{ explained_variance_score(predictions_linear,y_test)}")
print(f"线性模型平均绝对误差：{ mean_absolute_error(predictions_linear,y_test)}")
print(f"线性模型均误差：{ mean_squared_error(predictions_linear,y_test)}")

print(f"Xgboost模型方差得分：{ explained_variance_score(predictions_xgboost,y_test)}")
print(f"Xgboost模型平均绝对误差：{ mean_absolute_error(predictions_xgboost,y_test)}")
print(f"Xgboost模型均误差：{ mean_squared_error(predictions_xgboost,y_test)}")

