//定义控制器,注入自定义的服务
app.controller('brandController',function($scope,$controller,brandService){
    $controller('baseController',{$scope:$scope});//继承
    //查询品牌列表
    $scope.findAll=function(){
        brandService.findAll().success(
            function(data){
                $scope.list=data;
            }
        );
    };

    //分页
    $scope.findByPage=function(page,size){
        brandService.findByPage(page,size).success(
            function(data){
                //{"rows":[{"firstChar":"S","id":6,"name":"360"},{"firstChar":"Z","id":7,"name":"中兴"},{"firstChar":"M","id":8,"name":"魅族"},{"firstChar":"P","id":9,"name":"苹果"},{"firstChar":"V","id":10,"name":"VIVO"}],"total":22}
                $scope.list=data.rows;//显示当前页数据,拿出每页数据
                $scope.paginationConf.totalItems=data.total;//更新总记录数
            }
        );
    };

    //新建品牌（修改品牌也用）
    $scope.addBrand=function(){
        var object;//服务层对象
        if ($scope.entity.id!=null){//如果品牌id不为null表示修改保存
            object=brandService.updateBrand($scope.entity);//修改
        }else {
            object=brandService.addBrand($scope.entity);//修改
        }
        object.success(//调用服务
            function(data){
                if(data.success){
                    $scope.reloadList();//刷新页面
                }else{
                    alert(data.message);
                }
            }
        );
    };

    //查询实体
    $scope.findBrand=function(id){
        //服务名称.方法名称调用
        brandService.findBrand(id).success(
            function(data){
                $scope.entity=data;
            }
        )
    };


    $scope.deleBrands=function () {
        if (confirm("确定要删除吗？")) {
            brandService.deleBrands($scope.selectIds).success(
                function(data){
                    if(data.success){
                        $scope.reloadList();//刷新页面
                        $scope.selectIds=[];
                    }else{
                        alert(data.message);
                    }
                }
            )}


    };

    //分页条件查询
    $scope.searchEntity={};//初始化查询条件
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function (data) {//响应数据
                $scope.list=data.rows;//每页显示条数
                $scope.paginationConf.totalItems=data.total;//总记录数
                //修改reLoadList()方法
            }
        )
    }

});