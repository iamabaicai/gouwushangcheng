app.controller("searchController",function($scope,searchService,$location){
    $scope.searchMap = {keywords:"",category:"",brand:"",spec:{},price:"",pageNo:1,pageSize:20,sort:'',sortField:''};
    $scope.resultMap = {};
    $scope.search = function(){
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(function(response){
            $scope.resultMap = response;
            $scope.buildLabel();
        });
    }

    $scope.buildLabel = function() {
        $scope.pageLable = [];
        var firstPage = 1;
        var lastPage =  $scope.resultMap.totalPages;


        $scope.showPre = true;
        $scope.showPost = true;


        if($scope.resultMap.totalPages >5) {
            if($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                $scope.showPre = false;
            }else if($scope.searchMap.pageNo + 2 >= $scope.resultMap.totalPages ) {
                firstPage=$scope.resultMap.totalPages-4;
                $scope.showPost = false;
            }else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else {
            $scope.showPrePot = false;
            $scope.showPostPot = false;
        }

        for(var i=firstPage;i<=lastPage;i++) {
            $scope.pageLable.push(i);
        }
    }

    /*$scope.buildLabel = function() {
        $scope.pageLable = [];
        for(var i=1;i<=$scope.resultMap.totalPages;i++) {
            $scope.pageLable.push(i);
        }
    }*/

    /*  $scope.buildLabel = function() {
          $scope.pageLable = [];
          for(var i=1;i<$scope.resultMap.totalPages;i++) {
              $scope.pageLable.push(i);
          }
      }*/

    //分页查询
    $scope.queryByPage = function(page) {
        if(page <1 || page > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = page;
        $scope.search();
    }

    //排序
    $scope.searchSort = function(sort,sortField){
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    //品牌的隐藏
    $scope.keywordsIsBrand = function() {
        var brands = $scope.resultMap.brandList;
        for(var i=0;i<brands.length;i++) {
            var brand = brands[i];
            var brandName = brand.text;
            if($scope.searchMap.keywords.indexOf(brandName) >= 0) {
                return true;
            }
        }

        return false;
    }


    //获取首页传递的参数
    $scope.loadKeywords = function() {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    }



    $scope.addSearchItem = function(name,value) {
        if(name == 'brand' || name == 'category' || name == 'price') {
            $scope.searchMap[name] = value;
        }else {
            $scope.searchMap.spec[name] = value;
        }

        $scope.search();
    }

    $scope.removeItemSearch = function(name) {
        if(name == 'brand' || name == 'category'  || name == 'price') {
            $scope.searchMap[name] = '';
        }else {
            delete $scope.searchMap.spec[name] ;
        }

        $scope.search();
    }

    $scope.isEmptyObject = function() {
        return angular.equals({},$scope.searchMap.spec);
    }

    $scope.isFisrtPage = function() {
        return $scope.searchMap.pageNo == 1;
    }

    $scope.isLastPage = function() {
        return  $scope.searchMap.pageNo  == $scope.resultMap.totalPages;
    }

})