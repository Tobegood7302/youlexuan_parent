app.service('brandService', function ($http) {
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    }

    this.findPage = function (page, size) {
        return $http.get('../brand/findPage.do?page=' + page + '&size=' + size);
    }

    this.save = function (entity) {
        var methodName = "add";
        if (entity.id) {
            methodName = "update";
        }
        return $http.post("../brand/" + methodName + ".do", entity);
    }

    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    }

    this.dele = function (selectIds) {
        return $http.get("../brand/delete.do?ids=" + selectIds);
    }

    this.search = function (searchEntity, page, size) {
        return $http.post("../brand/search.do?page=" + page +"&size=" + size, searchEntity);
    }

    this.selectBrandList = function () {
        return $http.get("../brand/selectBrandList.do");
    }
})