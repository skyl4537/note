


    15.各层命名规约
        //Service/DAO 方法前缀
        get,list; count(统计值); save(*)/insert; remove(*)/delete; update;
        
        //领域模型命名规约
        数据对象: xxxDO, xxx为数据表名
        数据传输对象: xxxDTO, xxx为业务领域相关的名称
        展示对象: xxxVO, xxx为网页名称
        POJO: DO/DTO/BO/VO的统称, 禁止命名成 xxxPOJO