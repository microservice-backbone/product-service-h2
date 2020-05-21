#create product
curl -d '{"category":"Book","title":"Lorem ipsum dolor sit amet,",
           "subTitle":"10% discount at cart","brand":"Oreilly","rating":4,
           "shortDescription":"Lorem ipsum dolor","description":"BOOK of KLITORIS"}' \
      -H 'Content-Type: application/json' \
      -X POST http://localhost:8083/product

curl localhost:8083/products/101