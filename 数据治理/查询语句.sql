--���²鿴�������۶�
select months, sum(pro_sum) 
from homework.sales 
group by months;

--���²鿴����ע������
select months,users_new_register
from homework.users_allinfo 
order by months;

--���²鿴�������۶�
select months,city,pro_sum
from homework.sale_city;

--���²鿴���С���Ʒ�������۶�
select homework.sale_city.months,city,category, homework.sale_city.pro_sum
from homework.sale_city,homework.sale_cate 
where homework.sale_city.months = homework.sale_cate.months;

--���²鿴�Ա���Ʒ�������۶�
select homework.sale_sex.months,sex,category,homework.sale_sex.pro_sum
from homework.sale_sex,homework.sale_cate 
where homework.sale_sex.months = homework.sale_cate.months;
