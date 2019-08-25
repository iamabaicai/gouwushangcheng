//品牌控制层
app.controller('baseController', function ($scope) {

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 100,//总记录条数
        itemsPerPage: 10,//总页码
        perPageOptions: [5, 10, 15, 20],//下拉框选择每页显示条数
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.selectIds = [];//选中的ID集合

    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果是被选中,则增加到数组
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除
        }
    }

    //把json转为字符串，参数jsonString原json字符串,key要提取的键的值
    //str={id:1,text:"华硕"}例如这样的格式拿华硕str["text"]
    $scope.jsonToString = function (jsonString, key) {
        var jsons = JSON.parse(jsonString);//将json字符串转换为json对象
        var str = "";
        for (var i = 0; i < jsons.length; i++) {
            if (i>0) {
                str+="/"+jsons[i][key];
            }else {
                str +=jsons[i][key];
            }
        }
        return str;
    }

});