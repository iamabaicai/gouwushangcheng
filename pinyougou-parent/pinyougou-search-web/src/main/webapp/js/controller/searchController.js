app.controller("searchController", function ($scope, searchService, $location) {
//初始化搜索条件，封装为一个map集合
    $scope.searchMap = {keywords: "", category: "", brand: "", spec: {}, price: "", pageNum: 1, pageSize: 30,sortField:"",sort:""};
    $scope.search = function () {
        //将搜索的当前页码转换为整数类型，防止出现异常
        $scope.searchMap.pageNum= parseInt($scope.searchMap.pageNum) ;
        searchService.search($scope.searchMap).success(
            function (response) {
                //搜索返回的结果
                $scope.resultMap = response;
                //调用分页标签
                $scope.buildPageLabel();
            }
        );
    }


    //用户点击页面的搜索条件，给搜索条件添加值
    $scope.addSearchItem = function (key, value) {
        //如果点击的是分类或者是品牌那么给searchMap相应属性赋值
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        //调用search（）
        $scope.search();

    };

    //移除所选搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        //调用search（）
        $scope.search();
    }


    //创建分页标签
    $scope.buildPageLabel = function () {
        $scope.pageLabel = [];//新增分页栏属性
        //总页码
        var maxPageNum = $scope.resultMap.totalPages;

        var firstPage = 1;//开始页码
        var lastPage = maxPageNum;//截止页码

        $scope.firstDot=true;//前面有点为true，初始化都为true
        $scope.lastDot=true;//后边有点

        if ($scope.resultMap.totalPages > 5) {
            if ($scope.searchMap.pageNum <= 3) {
                //如果当前页小于等于3,那么最后一页是5
                lastPage = 5;
                $scope.firstDot=false;//页码前无省略号
            } else if ($scope.searchMap.pageNum >= lastPage - 2) {
                //如果当前页大于等于最大页码-2,后5页
                firstPage = maxPageNum - 4;
                $scope.lastDot=false;//页码后边没省略号
            } else {
                //显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;

            }
        } else {
            //总页码小于等于5页，页码前后都没有省略号
            $scope.firstDot=false;
            $scope.lastDot=false;

        }

        for (var i = firstPage; i <= lastPage; i++) {
            //将所有页码放到数组pageLabel中
            $scope.pageLabel.push(i);
        }
    };

    //根据页码查询,分页查询
    $scope.queryForPage = function (pageNum) {
        //校验页码,如果页码不合法，则不执行查询
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNum=pageNum;
        //调用方法
        $scope.search();
    }

    //页码样式可用
    $scope.isFirstPage=function () {
        //判断是否是首页
        if ($scope.searchMap.pageNum==1) {
            return true;
        }else{
            return false;
        }
    }
    $scope.isLastPage=function () {
        //判断是否是尾页
        if ($scope.searchMap.pageNum==$scope.resultMap.totalPages) {
            return true;
        }else{
            return false;
        }
    }
    //设置排序规则,sortField排序字段,sort排序规则(asc/desc)
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;

        //调用查询方法
        $scope.search();
    }
  //用户搜索的关键字是品牌，应该隐藏品牌选项
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i <$scope.resultMap.brandList.length ; i++) {
            //遍历品牌列表
            var brand= $scope.resultMap.brandList[i].text;
            var index= $scope.searchMap.keywords.indexOf(brand);
            if (index>=0){
                return true;
            }
        }
        return false;
    };
    //加载查询字符串
    $scope.loadKeywords=function () {
        //接收门户首页携带过来的搜索关键字参数
        $scope.searchMap.keywords=$location.search()['keywords'];

        //调用查询方法自动查询
        $scope.search();
    }
});