import { createContext, useEffect, useState } from "react";
import * as AWS from 'aws-sdk';
import { Amplify, Auth, API, Hub } from "aws-amplify";
import {config} from '../aws-config';
import { S3 } from 'aws-sdk';

const ServiceContext = createContext();


Amplify.configure({
    aws_cognito_region: config.REGION, // (required) - Region where Amazon Cognito project was created
    aws_user_pools_id: config.USER_POOL_ID, // (optional) -  Amazon Cognito User Pool ID
    aws_user_pools_web_client_id: config.APP_CLIENT_ID, // (optional) - Amazon Cognito App Client ID (App client secret needs to be disabled)
    aws_cognito_identity_pool_id: config.IDENTITY_POOL_ID, // (optional) - Amazon Cognito Identity Pool ID
    aws_mandatory_sign_in: 'enable', // (optional) - Users are not allowed to get the aws credentials unless they are signed in
    aws_cognito_verification_mechanisms: ["EMAIL"],
    aws_cognito_signup_attributes: ["EMAIL"],
    API:{
      endpoints:[
        {
          name: config.api.name,
          endpoint: config.api.baseUrl
        }
      ]
    }
  
  });




export const ServiceProvider = ({children}) => {
    const s3client = new S3({
        region: config.REGION
    });

    const defaltUser = {
        username: "User",
        attributes:{
            picture: "https://cdn.pixabay.com/photo/2016/04/27/18/33/minion-1357223__340.jpg"
        }
    };

    const defaultpostValue={
        id:"",
        title: "",
        massage: "",
        imgUrl:"",
        imgKey:"",
        username:""
    }

    const [user, setUser] = useState(defaltUser);

    const [post, setPost] = useState(defaultpostValue);

    const [posts, setPosts] = useState([]);

    const [isLoading, setisLoading] = useState();

    const [isAdmin, setIsAdmin] = useState(false);

    const [signedInUser, setSignedUser] = useState(false);

    const [myPost, setMyPost] = useState(false);

    Hub.listen("auth", (data) => {
        switch (data.payload.event) {
            case "signIn":
                return setSignedUser(true)
            case "signOut":
                return setSignedUser(false)
        }
    })

    useEffect(()=>{
        checkUser();
      },[signedInUser])
    
    function setMyPostPage(value){
        setMyPost(value);
    }

    async function checkUser() {
        try {
            setisLoading(true)
          const user = await Auth.currentAuthenticatedUser()
          getAWSTemporaryCreds(user);
          console.log(user);
          isAdminCheck(user);
          setUser(user);
          setisLoading(false)
        } catch (error) {
          console.log(error);
        }
      }

      function isAdminCheck(isadminUser){
        const idTokenPayload = isadminUser.getSignInUserSession().getIdToken().decodePayload();
        console.log(idTokenPayload);
        const cognitoGroups = idTokenPayload['cognito:groups'];
        console.log(cognitoGroups);
        var admin = false;
        if(cognitoGroups){
            admin = cognitoGroups.includes("admins");
            console.log(admin);
        }
        setIsAdmin(admin);
      }

      async function refreshCredentials(){
        return new Promise((resolve, reject)=>{
            (AWS.config.credentials).refresh(err =>{
                if (err) {
                    reject(err)
                } else {
                    resolve()
                }
            })
        })
       }


       async function getAWSTemporaryCreds(user){
        const cognitoIdentityPool = `cognito-idp.${config.REGION}.amazonaws.com/${config.USER_POOL_ID}`; 
        AWS.config.credentials = new AWS.CognitoIdentityCredentials({
            IdentityPoolId: config.IDENTITY_POOL_ID,
            Logins: {
                [cognitoIdentityPool]: user.getSignInUserSession().getIdToken().getJwtToken()
            }
        }, {
            region: config.REGION
        });
        await refreshCredentials();
     }


     async function getPostById(id){
        setisLoading(true)
        try {
            const apiName = config.api.name;
            const path = "/posts/"+id;
            const myInit = {
                headers: {
                Authorization: `Bearer ${(await Auth.currentSession())
                  .getIdToken()
                  .getJwtToken()}`
              }
            };
              const response= await API.get(apiName, path, myInit);
              console.log(response);
              setPost(response);
          } catch (error) {
            console.log(error);
          }
          setisLoading(false)
     }

     async function getPosts(){
        setisLoading(true)
        try {
            const apiName = config.api.name;
            const path = "/posts";
            const myInit = {
                headers: {
                Authorization: `Bearer ${(await Auth.currentSession())
                  .getIdToken()
                  .getJwtToken()}`
              }
            };
              const response= await API.get(apiName, path, myInit);
              console.log(response);
              setPosts(response);
          } catch (error) {
            console.log(error);
          }
          setisLoading(false)
     }

     async function getPostsByUserName(){
        setisLoading(true)
        try {
            const apiName = config.api.name;
            const path = "/posts?username="+user.username;
            const myInit = {
                headers: {
                Authorization: `Bearer ${(await Auth.currentSession())
                  .getIdToken()
                  .getJwtToken()}`
              }
            };
              const response= await API.get(apiName, path, myInit);
              console.log(response);
              setPosts(response);
          } catch (error) {
            console.log(error);
          }
          setisLoading(false)
     }


     async function createPost(postData){
        setisLoading(true)
        const apiName = config.api.name;
        const path = '/posts';
        const myInit = {
            body:postData,
            headers: {
            Authorization: `Bearer ${(await Auth.currentSession())
              .getIdToken()
              .getJwtToken()}`
          }
        };
        try {
            const response = await API.post(apiName, path, myInit);
            console.log(response);
            setPost(postData);
        } catch (error) {
            console.log(error);
        }
        setisLoading(false)
     }


     async function updatePost(postData){
        setisLoading(true)
        const apiName = config.api.name;
        const path = '/posts/'+postData.id;
        const myInit = {
            body:postData,
            headers: {
            Authorization: `Bearer ${(await Auth.currentSession())
              .getIdToken()
              .getJwtToken()}`
          }
        };
        try {
            const response = await API.put(apiName, path, myInit);
            setPost(postData);
            console.log(response);
        } catch (error) {
            console.log(error);
        }
        setisLoading(false)
     }

     async function deletePostById(id, imgkey){
        if(window.confirm("Are you sure you want to delete?")){
            setisLoading(true)
        try { 
          const apiName = config.api.name;
          const path = "/posts/"+id;
          const myInit = {
              headers: {
              Authorization: `Bearer ${(await Auth.currentSession())
                .getIdToken()
                .getJwtToken()}`
            }
          };
            const response= await API.del(apiName, path, myInit);
            if(imgkey!==""){
                await deleteImage(imgkey, config.POSTS_PHOTOS_BUCKET);
            }    
            setPosts(posts.filter((item)=> item.id!==id));
            console.log(response); 
        } catch (error) {
            console.log(error);
        }
        setisLoading(false)
        }  
      }

     function generateRandomid(){
        return Math.random().toString(36).slice(2);
    }

    async function uploadimage(file, bucket){
        setisLoading(true)
        const filename = `${file.name}_${generateRandomid()}`;
        try {
            const uploadResult = await s3client.upload({
                Bucket: bucket,
                Key: filename,
                Body: file,
                ACL: 'public-read',

            }).promise();
            console.log(uploadResult);
            return uploadResult;
        } catch (error) {
            console.log(error);
        }
        setisLoading(false)
    }

     async function deleteImage(key, bucket){
        const filename = key;
        try {
            const deleteresponse = await s3client.deleteObject({
                Bucket: bucket,
                Key: filename
            }).promise();
            console.log(deleteresponse);
        } catch (error) {
            console.log(error);
        }
    }

    async function updateUserProfilePicture(attribute){
        setisLoading(true)

        try {  
            const response = await uploadimage(attribute, config.PROFILE_PHOTOS_BUCKET)
            if(user.attributes.picture){
                const imgKey = user.attributes.picture.split("/").at(-1);
                console.log(imgKey)
                deleteImage(imgKey, config.PROFILE_PHOTOS_BUCKET)
            }
            const attr = {
                picture: response.Location,
            } 
            await Auth.updateUserAttributes(user, attr)
            checkUser();
        } catch (error) {
            
        }
        setisLoading(false)
    }

    async function signOut(){
        try {
            await Auth.signOut();
            setUser(defaltUser);
            setPost(defaultpostValue);
            setPosts([]);
          } catch (error) {
            console.log('error signing out: ', error);
          }
    }

    return (<ServiceContext.Provider value={{
        user,
        post,
        posts,
        defaultpostValue,
        isLoading,
        isAdmin,
        myPost,
        checkUser,
        getPostById,
        getPosts,
        getPostsByUserName,
        uploadimage,
        deleteImage,
        createPost,
        updatePost,
        deletePostById,
        updateUserProfilePicture,
        signOut,
        setMyPostPage
    }}>
        {children}
    </ServiceContext.Provider>
    )
}

export default ServiceContext;
