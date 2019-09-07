app.controller("indexController",function ($scope,$controller,loginService) {
    //获取用户名称
    $scope.getLoginName=function () {
        loginService.getLoginName().success(
            function (response) {
                $scope.name=response.loginName;

            }
        )
    }
});