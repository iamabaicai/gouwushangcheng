app.service("contentService",function ($http) {
    this.findByCategoryId=function(categoryId){
        return $http.get("../content/findContentByContentCategoryId.do?categoryId="+categoryId);
    }



});