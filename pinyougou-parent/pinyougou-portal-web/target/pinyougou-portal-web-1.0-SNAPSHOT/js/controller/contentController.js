//广告控制层（运营商后台）
app.controller("contentController",function($scope,contentService){
    $scope.contentList=[];//广告集合
    $scope.findByCategoryId=function(categoryId){
        contentService.findByCategoryId(categoryId).success(
            function(response){
                //把tb_content表广告分类外键categoryId作为数组索引，存储各类广告的数组
                //contentList=[[],[轮播图列表],[今日推荐],[活动专区],[]]二维数组
                $scope.contentList[categoryId]=response;
            }
        );
    }

    //门户首页index.html点击搜索携带参数跳转搜索页面search.html
    $scope.toSearchPage=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});
