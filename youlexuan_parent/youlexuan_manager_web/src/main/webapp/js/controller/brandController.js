app.controller("brandController", function($scope, $controller, brandService) {
    // 继承
    $controller('baseController', {
        $scope:$scope
    });

    // 查询所有
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    };

    // 分页
    $scope.findPage = function (page, size) {
        brandService.findPage().success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    }

    // 添加 && 修改
    $scope.save = function () {
        brandService.save($scope.entity).success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })
    }

    // 修改查询后台数据
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    }

    // 删除
    $scope.dele = function (){
        brandService.dele($scope.selectIds).success(function (response) {
            if (response.success) {
                $scope.reloadList();
                $scope.selectIds = [];
            } else {
                alert(response.message);
            }
        })
    }

    // 查询
    $scope.searchEntity = {};
    $scope.search = function (page, size) {
        brandService.search($scope.searchEntity, page, size).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;//更新总记录数`
        })
    }



})