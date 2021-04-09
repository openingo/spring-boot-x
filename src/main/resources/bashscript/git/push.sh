#!/bin/bash
echo "Enter commit message:"
# shellcheck disable=SC2162
read commit_message
echo "The message is: $commit_message"
echo "======START PUSH========"
# your api module
api_module="demo-api"
# your service module
service_module="demo-service-openinngo-org"
# shellcheck disable=SC2207
# shellcheck disable=SC2006
# shellcheck disable=SC2116
priority_modules=(`echo ${api_module}` `echo ${service_module}`)
# shellcheck disable=SC2207
# shellcheck disable=SC2006
# shellcheck disable=SC2116
full=($(find . -type d -maxdepth  1  ! \( -name "`echo ${api_module}`" -o -name "`echo ${service_module}`" -o -name ".*" \) -exec ls -d {} \;))
# shellcheck disable=SC2206
sorted_modules=(${priority_modules[@]} ${full[*]})
# shellcheck disable=SC2068
for file in ${sorted_modules[@]};
do
if [ -d "$file" ]
then 
  # shellcheck disable=SC2046
  # shellcheck disable=SC2164
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  cd `echo "$file"`
  # shellcheck disable=SC2006
  current_branch=`git symbolic-ref --short -q HEAD`
  echo "→ → → 【 $file 】at $current_branch"
  git add .
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  git commit  -m "`echo "${commit_message}"`"
  echo "→ → → pulling from $current_branch"
  git pull origin "${current_branch}"
  # shellcheck disable=SC2028
  echo '\n'
  echo "→ → → pushing to $current_branch"
  git push origin "${current_branch}"
  # shellcheck disable=SC2028
  echo "\n"
  cd ..
fi
done
echo "======PUSH OVER========"