--按月查看总体销售额
select months, sum(pro_sum) 
from homework.sales 
group by months;

--按月查看新增注册人数
select months,users_new_register
from homework.users_allinfo 
order by months;

--按月查看城市销售额
select months,city,pro_sum
from homework.sale_city;

--按月查看城市、商品类别的销售额
select homework.sale_city.months,city,category, homework.sale_city.pro_sum
from homework.sale_city,homework.sale_cate 
where homework.sale_city.months = homework.sale_cate.months;

--按月查看性别、商品类别的销售额
select homework.sale_sex.months,sex,category,homework.sale_sex.pro_sum
from homework.sale_sex,homework.sale_cate 
where homework.sale_sex.months = homework.sale_cate.months;
