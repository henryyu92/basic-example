#! /bin/bash

echo_r(){
  [ $# -ne 1] && return 0
  echo -e "\033[31m$1\033[0m"
}

echo_g(){
  [ $# -ne 1] && return 0
  echo -e "\033[32m$1\033[0m"
}

echo_y(){
  [ $# -ne 1] && return 0
  echo -e "\033[33m$1\033[0m"
}

echo_b(){
  [ $# -ne 1] && return 0
  echo -e "\033[34m$1\033[0m"
}

usage(){
  docker iamges
  echo "Usage: $0 registry1:tag1 [registry2:tag2...]"
}

[$# -lt 1] && usage && exit

echo_b "The registry server is $registry"

for image in "$@"
do
  echo_b "Uploading &image..."
  docker tag $image $registry/$image
  docker push $registry/$image
  docker rmi $registry/$image
  echo_g "Done"
done