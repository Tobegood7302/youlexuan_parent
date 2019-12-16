app.controller('baseController', function ($scope) {

    // 分页插件
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [5, 10, 20, 30],
        onChange: function () {
            // 切换页码, 重新加载
            $scope.reloadList();

        }
    };

    // 选中
    $scope.selectIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);
        }
    }

    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.isSelected = function (id) {
        return $scope.selectIds.indexOf(parseInt(id)) != -1;
    }

    // 全选
    $scope.selectAll = function ($event) {
        // 获取当前全选框的选中状态
        var state = $event.target.checked;
        // 遍历复选框
        $('.eachbox').each(function (index, obj) {
            // 改变复选框选中状态
            var state_obj = obj.checked;
            obj.checked = state;
            // 改变选中数组
            var id = parseInt($(obj).parent().next().text());
            if (state) {
                if (!state_obj) {
                    $scope.selectIds.push(id);
                }
            } else {
                var idx = $scope.selectIds.indexOf(id);
                $scope.selectIds.splice(idx, 1);
            }
        })
    }

    $scope.isAllSelected = function () {
        var state = true;
        $('.eachbox').each(function (index, obj) {
            var id = parseInt($(obj).parent().next().text());
            if ($scope.selectIds.indexOf(id) == -1) {
                state = false;
            }
        })
        return state;
    }

    $scope.jsonToString = function (jsonString, key) {
        var value = '';
        var json = JSON.parse(jsonString);
        for (var i=0; i < json.length; i++) {
            if (i > 0) {
                value += ',';
            }
            value += json[i][key];
        }
        return value;
    }

    // 查询某个键在json字符串中是否存在
    $scope.findObjByKey = function (list, key, keyVal) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == keyVal) {
                return list[i];
            }
        }
        return null;
    }
})