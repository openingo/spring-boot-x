#!/bin/bash
echo "Enter checkout branch:"
# shellcheck disable=SC2162
read checkout_branch
echo "The checkout_branch is: $checkout_branch"
echo "======START CHECKOUT========"
# shellcheck disable=SC2045
# shellcheck disable=SC2006
for file in `ls`;
do
if [ -d "$file" ]
then 
  # shellcheck disable=SC2046
  # shellcheck disable=SC2164
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  cd `echo "$file"`
  git checkout "${checkout_branch}"
  # shellcheck disable=SC2028
  echo "\n"
  cd ..
fi
done
echo "======CHECKOUT OVER========"