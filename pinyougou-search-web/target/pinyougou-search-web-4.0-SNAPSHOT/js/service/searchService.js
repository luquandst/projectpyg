app.service("searchService",function ($http) {

    this.search=(searchMap)=>{
        return $http.post("./itemSearch/search.do",searchMap);
    }
})