app.controller('contentController', function ($scope, contentService) {

    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (resp) {
            if (resp) {
                $scope.contentList = resp;
            }else {
                alert("加载图片失败");
            }
        })
    }
    
    $scope.search = function () {
        location.href = 'http://localhost:9007/search.html#?keywords=' + $scope.searchKeywords;
    }


});