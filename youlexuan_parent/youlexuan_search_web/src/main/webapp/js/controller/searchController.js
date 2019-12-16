app.controller('searchController', function ($scope, $location, searchService) {

    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (resp) {
            if (resp) {
                $scope.resultMap = resp;

                // 构建分页标签
                buildPageLabel();
            }
        })
    }

    // 构建分页
    buildPageLabel = function() {

        // 标签列表
        $scope.list = [];

        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;

        // 前面的省略号
        $scope.firstDot = false;
        // 后面的省略号
        $scope.lastDot = false;

        if ($scope.resultMap.totalPages > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;

                $scope.lastDot = true;
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {
                firstPage = $scope.resultMap.totalPages - 4;

                $scope.firstDot = true;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

                $scope.firstDot = true;
                $scope.lastDot = true;
            }
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.list.push(i);
        }

    }


    // 定义搜索对象
    $scope.searchMap = {'keywords' : '', 'category' : '', 'brand' : '', 'spec' : {}, 'price' : '', 'sortName' : '', 'sortVal' : '', 'pageNo' : 1, 'pageSize' : 20};

    // 添加搜索条件
    $scope.addSearch = function (key, value) {
        if (key != 'category'&& key != 'brand' && key != 'price') {
            $scope.searchMap.spec[key] = value;
        } else {
            $scope.searchMap[key] = value;
        }

        $scope.search();

    }

    // 删除搜索条件
    $scope.removeSearch = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }

        $scope.search();
    }

    // 搜索按钮
    $scope.reload = function () {
        // 重置搜索条件
        $scope.searchMap.category = '';
        $scope.searchMap.brand = '';
        $scope.searchMap.spec = {};
        $scope.searchMap.price = '';
        $scope.searchMap.sortName = '';
        $scope.searchMap.sortVal = '';
        $scope.searchMap.pageNo = 1;
        // 搜索
        $scope.search();
    }

    // 排序
    $scope.sortSearch = function (sortName, sortVal) {
        $scope.searchMap['sortName'] = sortName;
        $scope.searchMap['sortVal'] = sortVal;

        $scope.search();
    }

    // 分页
    $scope.queryByPage = function (pageNo) {
        // 将pageNo转为integer(防止input输入之后后台类型转换错误)
        pageNo = parseInt(pageNo);
        if (pageNo < 1) {
            // 小于1查询第一页
            $scope.searchMap.pageNo = 1;
        } else if (pageNo > $scope.resultMap.totalPages) {
            // 大于最大页码查询最后一页
            $scope.searchMap.pageNo = $scope.resultMap.totalPages;
        } else {
            // 正常查询
            $scope.searchMap.pageNo = pageNo;
        }

        $scope.search();
    }

    $scope.initSearch = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
});