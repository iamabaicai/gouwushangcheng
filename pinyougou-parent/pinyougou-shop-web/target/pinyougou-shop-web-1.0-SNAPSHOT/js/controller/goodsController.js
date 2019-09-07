//控制层
app.controller('goodsController', function ($scope, $controller, $location,
                                            goodsService, uploadService,
                                            itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()["id"];//获取跳转页面传递的参数
        alert(id);
        if (id == null) {//如果id为null就不再继续查询
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //回显富文本框数据
                editor.html(response.goodsDesc.introduction);
                //回显图片字符串需要转换对象；测试id=149187842867941
               //[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhV1qJuWAfJ4wAAFe0SaR4Ek302.jpg"},{"color":"黑色",
                //"url":"http://192.168.25.133/group1/M00/00/00/wKgZhV1qJv2AInNBAALmZaO_aF0317.jpg"}]
                $scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);

                //扩展属性
                //[{"text":"内存大小","value":"4G运存"},{"text":"颜色","value":"流光炫彩"}]
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.goodsDesc.customAttributeItems);

                //读取规格属性返回的字符串
                //[{"attributeValue":["移动4G","联通4G"],"attributeName":"网络"},{"attributeValue":["32G","64G"],"attributeName":"机身内存"}]
                $scope.entity.goodsDesc.specificationItems=JSON.parse(response.goodsDesc.specificationItems);

                //SKU列表
                for (var i = 0; i <response.itemList.length ; i++) {
                    //spec:[{"网络":"联通4G","机身内存":"64G"},{"网络":"联通4G","机身内存":"32G"}]

                    $scope.entity.itemList[i].spec= JSON.parse(response.itemList[i].spec)

                }
            }
        );
    };



    //保存
    $scope.save = function () {
        //提取富文本框的内容
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //alert('保存成功');跳转到商品列表查看店家自己的商品
                    location.href="goods.html";//
                    $scope.entity={};//清空实体类
                    editor.html=("");//清空富文本编辑框
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        //调用方法查看
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,
            "attributeName", specName);
        if (obj==null){
            return false;
        }else {
           var index= obj.attributeValue.indexOf(optionName);
           if (index>=0){
               return true;
           } else {
               return false;
           }
        }
    };

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象
    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //商品状态0,1,2,3对应数组索引的元素
    $scope.status = ["未审核", "审核通过", "审核未通过", "关闭"]

    $scope.categoryArray = [];//分类数组
    $scope.findCategoryArray = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    //把对象的id作为做索引，值为name
                    $scope.categoryArray[response[i].id] = response[i].name;
                }
            }
        )
    };

    //读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        );
    };

    //文件上传
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    //如果上传成功，取出url
                    $scope.image_entity.url = response.message;
                    alert(image_entity.url);
                } else {
                    alert(response.message);
                }
            }
        ).error(function () {
            alert("上传发生错误!!!");
        });
    };

    //定义页面实体结构,展示图片列表
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //删除图片列表图片，但是fastDFS服务器还在
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    //一级分类列表，查询parentId=0的分类对象列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                //一级分类列表，查询parentId=0的分类对象列表
                $scope.ItemCat1List = response;
            }
        )
    }

    //二级分类列表,检测一级分类列表发生改变加载二级分类列表
    $scope.$watch("entity.goods.category1Id", function (newVal, oldVal) {
        itemCatService.findByParentId(newVal).success(
            function (response) {
                //二级分类列表
                $scope.ItemCat2List = response;
            }
        )
    })

    //三级分类列表,检测二级分类列表发生改变加载三级分类列表
    $scope.$watch("entity.goods.category2Id", function (newVal, oldVal) {
        itemCatService.findByParentId(newVal).success(
            function (response) {
                //三级分类列表
                $scope.ItemCat3List = response;
            }
        )
    })

    //监测三级分类发生变化获取它的模板对象，然后获取模板Id
    $scope.$watch("entity.goods.category3Id", function (newVal, oldVal) {
        itemCatService.findOne(newVal).success(
            //通过分类Id查询tb_item_cat，response是TbItemCat对象拿出他的字段typeId
            //赋值给组合实体类的成员goods.typeTemplateId
            function (response) {
                //三级分类列表
                $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
            }
        )
    });
    //监测模板id发生变化从tb_type_template表查找关联模板
    $scope.$watch("entity.goods.typeTemplateId", function (newVal, oldVal) {
        typeTemplateService.findOne(newVal, oldVal).success(
            function (response) {
                //[{"id":1,"text":"联想"},{"id":3,"text":"三星"},{"id":2,"text":"华为"},{"id":5,"text":"OPPO"},
                // {"id":4,"text":"小米"},{"id":9,"text":"苹果"},{"id":8,"text":"魅族"},{"id":6,"text":"360"},
                // {"id":10,"text":"VIVO"},{"id":11,"text":"诺基亚"},{"id":12,"text":"锤子"}]
                $scope.typeTemplate = response;//模板对象
                //品牌列表
                $scope.typeTemplate.brandIds = JSON.parse(response.brandIds);

                //如果页面传递id=null说明是新增才执行查询扩展属性
                if ($location.search()['id'] == null) {
                    //获取模板对象的扩展属性
                    //[{"text":"内存大小"},{"text":"颜色"}]
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                }
            }
        );
        //查询规格列表
        typeTemplateService.findSpecList(newVal).success(
            function (response) {
                //[{"id":27,"text":"网络","options":[{},{}]},]
                $scope.specList = response;
            }
        )
    });


    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
    //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
    // {"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
    $scope.updateSpecAttribute = function ($event, specName, optionName) {
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,
            "attributeName", specName);
        if (event.target.checked) {
            //如果勾选
            if (obj == null) {
                //obj={"attributeName":specName,"attributeValue":[optionName]}
                $scope.entity.goodsDesc.specificationItems.push({
                    "attributeName": specName,
                    "attributeValue": [optionName]
                })
            } else {
                obj.attributeValue.push(optionName);
            }
        } else {
            //如果取消勾选
            var index = obj.attributeValue.indexOf(optionName);
            //从"attributeValue":["6寸","5寸"]中去掉选择的规格选择5寸/6寸
            obj.attributeValue.splice(index, 1);
            if (obj.attributeValue <= 0) {
                var i = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                $scope.entity.goodsDesc.specificationItems.splice(i, 1);
            }
        }
    };

    //创建规格列表，深克隆
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始

        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = $scope.addColumn($scope.entity.itemList,
                items[i].attributeName,
                items[i].attributeValue);
        }
    };

    //深克隆列表
    $scope.addColumn = function (list, columnName, conlumnValues) {
        var newList = [];//新的集合
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
});	
