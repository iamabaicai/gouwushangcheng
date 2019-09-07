app.service("brandService",function ($http) { //定义服务
    this.findAll=function () {//查询品牌列表
        return $http.get('../brand/findAllBrands.do');
    };

    this.findByPage=function (page,size) {
        return $http.get('../brand/findByPage.do?currentPage='+page +'&pageSize='+size);
    };

    this.findBrand=function (id) {
        return $http.get("../brand/findBrandById.do?id="+id);
    };

    //增加
    this.addBrand=function(entity){
        return  $http.post('../brand/addBrand.do',entity );
    };

    //修改
    this.updateBrand=function(entity){
        return  $http.post('../brand/updateBrand.do',entity );
    };


    //删除
    this.deleBrands=function (ids) {
        return $http.get("../brand/deleteBrands.do?ids="+ids);
    };

    //搜索
    this.search=function(page,size,searchEntity){
        return $http.post("../brand/search.do?currentPage="+page+"&pageSize="+size, searchEntity);
    }

    this.selectBrandList=function () {
        return $http.get("../brand/selectBrandList.do");
    }

});