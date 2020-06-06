# update product

curl localhost:8083/product/10

curl -d '{"id":10,"category":"Fashion","title":"Lorem",
          "subTitle":"free cargo","brand":"Dyson","rating":2,
          "shortDescription":"Lorem ipsum dolor sit amet,",
          "description":"ZLITORIS KOMINOTORIS"}' \
     -H 'Content-Type: application/json' \
     -X PUT http://localhost:8083/product/10

curl localhost:8083/product/10
