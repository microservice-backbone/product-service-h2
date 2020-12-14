# read requests

echo "get hostname (meaningful in a server)"
curl localhost:8083/dummy

echo ""
curl localhost:8083/dummy/Tansu

#read
printf "\n review/1 >>"
curl localhost:8083/product/1

printf "\n review/101 : >>"
curl localhost:8083/product/101

curl localhost:8083/products
curl localhost:8083/products/page/0/size/1


#save
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

# delete product

curl -H 'Content-Type: application/json' -X DELETE http://localhost:8083/product/100

curl localhost:8083/product/100