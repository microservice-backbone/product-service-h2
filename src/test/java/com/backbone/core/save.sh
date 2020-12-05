#create product
curl -d '{"category":"Book","title":"Lorem ipsum dolor sit amet,",
           "subTitle":"10% discount at cart","brand":"Oreilly","rating":4,
           "shortDescription":"Lorem ipsum dolor","description":"BOOK of ZLITORIS"}' \
      -H 'Content-Type: application/json' \
      -X PUT http://localhost:8083/product

curl localhost:8083/product/101

#update
curl localhost:8083/product/10

curl -d '{"id":10,"category":"Fashion","title":"Lorem",
          "subTitle":"free cargo","brand":"Dyson","rating":2,
          "shortDescription":"Lorem ipsum dolor sit amet,",
          "description":"ZLITORIS KOMINOTORIS"}' \
     -H 'Content-Type: application/json' \
     -X PUT http://localhost:8083/product

curl -H 'Content-Type: application/json' \
     -X PUT http://localhost:8083/product

curl localhost:8083/product/10