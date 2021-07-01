
--用户信息表
drop table if exists homework.user_info;
create table homework.user_info
(
	user_id INT primary key,
	phone_number VARCHAR(11),
	user_name VARCHAR(5),
	ID_Number VARCHAR(18),
	Register_time DATE,
	pet_name VARCHAR(12),
	sex VARCHAR(2),
                age int,
                email VARCHAR(12)
);
-- Add comments to the table 
comment on table homework.user_info
  is '用户信息表';
-- Add comments to the columns 
comment on column homework.user_info.user_id
  is '用户id';
comment on column homework.user_info.phone_number
  is '手机号码';
 comment on column homework.user_info.user_name
  is '用户姓名';
 comment on column homework.user_info.ID_Number
  is '身份证号码';
 comment on column homework.user_info.Register_time
  is '注册时间';
 comment on column homework.user_info.pet_name
  is '用户昵称';
  comment on column homework.user_info.sex
  is '用户性别';
  comment on column homework.user_info.AGE
  is '用户年龄';
  comment on column homework.user_info.email
  is '邮箱地址';


--用户地址表
drop table if exists homework.user_address;
create table homework.user_address
(
	user_id int primary key,
	provience VARCHAR(5),
	city VARCHAR(5),
	county VARCHAR(5),
	specific_address VARCHAR(10)
);
-- Add comments to the table 
comment on table homework.user_address
  is '用户地址表'; 
 -- Add comments to the columns 
comment on column homework.user_address.user_id
  is '用户id';
 comment on column homework.user_address.provience
  is '省份';
 comment on column homework.user_address.city
  is '市';
 comment on column homework.user_address.county
  is '县';
 comment on column homework.user_address.specific_address
  is '具体地址';
 
 --用户登陆表
 drop table if exists homework.login;
 create table homework.login
 (
 	user_id INT primary key ,
 	phone_number VARCHAR(11),
 	passwords VARCHAR(10)
 );
 -- Add comments to the table 
comment on table homework.login
  is '用户登陆表'; 
 -- Add comments to the columns 
comment on column homework.login.user_id
  is '用户id';
 comment on column homework.login.phone_number
  is '用户账号';
 comment on column homework.login.passwords
  is '用户密码';
 
 --用户购物车表
 drop table if exists homework.shopping_cart;
 create table homework.shopping_cart
 (
 	user_id INT ,
 	sku_id INT ,
 	numbers INT,
 	total_price INT
 );
 -- Add comments to the table 
comment on table homework.shopping_cart
  is '用户购物车表'; 
 -- Add comments to the columns 
comment on column homework.shopping_cart.user_id
  is  '用户id';
comment on column homework.shopping_cart.sku_id
  is  '商品id';
 comment on column homework.shopping_cart.numbers
  is  '商品数量';
 comment on column homework.shopping_cart.total_price
  is  '总价格';
 
 --订单表
 drop table if exists homework.orders;
 create table homework.orders
 (
 	id INT primary key,
 	user_id INT ,
 	sku_id INT ,
 	numbers INT,
 	total_prices INT,
 	order_month_time INT
 );
  -- Add comments to the table 
comment on table homework.orders
  is '订单信息表'; 
comment on column homework.orders.id
  is '订单id';
comment on column homework.orders.user_id
  is '用户id';
comment on column homework.orders.sku_id
  IS '商品id';
comment on column homework.orders.numbers
  IS '商品数量';
comment on column homework.orders.total_prices
  IS  '总价格';
comment on column homework.orders.order_month_time
  IS  '创建订单时间';

 --销售额总表
 DROP TABLE IF EXISTS homework.sales;
 CREATE TABLE homework.sales
 (
 	sex varchar(2),
 	months int,
 	city varchar(4),
 	category varchar(4),
 	pro_sum int
 );
-- Add comments to the table 
comment on table homework.sales
  is '销售信息总表'; 
comment on column homework.sales.sex
  IS  '性别';
comment on column homework.sales.months
  IS  '月份';
comment on column homework.sales.city
  IS  '城市';
comment on column homework.sales.category
  IS  '商品类别';
comment on column homework.sales.pro_sum
  IS  '销售总额';
 
 --按城市分类的销售表
 DROP TABLE IF EXISTS homework.sale_city;
 CREATE TABLE homework.sale_city
 (
 	city VARCHAR(4),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table homework.sale_city
  is '城市商品销售总表'; 
comment on column homework.sale_city.city
  IS '城市';
 comment on column homework.sale_city.months
  IS '月份';
 comment on column homework.sale_city.pro_sum
  IS '总额';
 
 --按性别分类的销售表
 DROP TABLE IF EXISTS homework.sale_sex;
 CREATE TABLE homework.sale_sex
 (
 	sex VARCHAR(2),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table homework.sale_sex
  is '按性别分类商品销售总表'; 
comment on column homework.sale_sex.sex
  IS '性别';
comment on column homework.sale_sex.months
  IS '月份';
comment on column homework.sale_sex.pro_sum
  IS '销售总额';
 
 --按商品类别分的销售总额表
 DROP TABLE IF EXISTS homework.sale_cate;
 CREATE TABLE homework.sale_cate
 (
 	category VARCHAR(4),
 	months int,
 	pro_sum int
 );
 -- Add comments to the table 
comment on table homework.sale_cate
  is '按商品类别商品销售总表'; 
comment on column homework.sale_cate.category
  IS '商品类别';
comment on column homework.sale_cate.months
  IS '月份';
comment on column homework.sale_cate.pro_sum
  IS '销售总额';
 
 --商家信息
DROP TABLE IF EXISTS homework.seller_info;
CREATE TABLE homework.seller_info
(
	seller_id INT PRIMARY KEY,
	name VARCHAR(5),
	account VARCHAR(10),
	passwords VARCHAR(10)
);
-- Add comments to the table 
comment on table homework.seller_info
  is '卖家信息表'; 
comment on column homework.seller_info.seller_id
  IS  '卖家id';
comment on column homework.seller_info.passwords
  IS  '密码';
comment on column homework.seller_info.account
  IS  '账户';

--商品种类表
DROP TABLE IF EXISTS homework.product_cate;
CREATE TABLE homework.product_cate
(
	spu_id int PRIMARY KEY,
	product_name VARCHAR(5),
	category VARCHAR(4)
);
-- Add comments to the table 
comment on table homework.product_cate
  is '商品种类表';
comment on column homework.product_cate.spu_id
  IS 'id';
comment on column homework.product_cate.product_name
  IS '商品名称';
comment on column homework.product_cate.category
  IS '商品类别';

 --卖家商品信息表
 DROP TABLE IF EXISTS homework.seller_pro;
 CREATE TABLE homework.seller_pro
 (
 	sku_id int PRIMARY KEY,
 	spu_id int ,
 	seller_id int ,
 	numbers int,
 	prices int
 );
 -- Add comments to the table 
comment on table homework.seller_pro
 IS '卖家商品信息表';
comment on column homework.seller_pro.sku_id
 IS '商品id';
comment on column homework.seller_pro.spu_id
 IS '商品类别id';
comment on column homework.seller_pro.seller_id
 IS '商家id';
comment on column homework.seller_pro.numbers
 IS '库存量';
comment on column homework.seller_pro.prices
 IS '价格';

drop table if exists homework.users_allinfo;
create table homework.users_allinfo
(
	months int,
	users_new_register int,
	users_registered int
);
-- Add comments to the table 
comment on table homework.users_allinfo
  is '用户信息统计表';
-- Add comments to the columns 
comment on column homework.users_allinfo.months
  is '月份';
comment on column homework.users_allinfo.users_new_register
  is '新增注册人数';
comment on column homework.users_allinfo.users_registered
  is '用户总数';
