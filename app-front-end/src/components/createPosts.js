import { useState, useRef, React, useEffect, useContext} from "react";
import { config as appConfig} from '../aws-config'
import SimpleMdeReact from "react-simplemde-editor";
import "easymde/dist/easymde.min.css";
import { useNavigate, useParams } from "react-router-dom";
import ServiceContext from '../context/ServiceContext';


const initialState = {title: "", massage: ""}
function CreatePost() {

    const {user, uploadimage, createPost, updatePost, deleteImage, isLoading, post} = useContext(ServiceContext);
    const params = useParams();
    const [currentPost, setPost] = useState(initialState);
    const [image, setImage] = useState(null);
    const [isupdatePost, setisupdatePost] = useState(false);
    const imageFileInput = useRef(null);
    const { title, massage } = currentPost;
    const nav = useNavigate();
    const editPostId = params.id;
  
    useEffect(()=>{
        if(editPostId){
            setisupdatePost(true);
            setPost(post)
        } else{
            setisupdatePost(false);
        }
    },[editPostId])


    function onChange(e) {
        setPost(() => ({
           ...currentPost , [e.target.name]: e.target.value
        }))
    }


    async function createNewPost(){
        if (!title || !massage) return;
        currentPost.imgUrl = "";
        currentPost.imgKey = "";
        if(image){
            const uploadResponse = await uploadimage(image, appConfig.POSTS_PHOTOS_BUCKET);
            currentPost.imgUrl = uploadResponse.Location;
            currentPost.imgKey = uploadResponse.Key
            //await Storage.put(filename, image);    
        }
        currentPost.username = user.username;
        const result = await createPost(currentPost);
        console.log("new post"+result);
        nav(`/view-posts/${result}`);
    }

    async function updatecurrentPost(){
        if (!title || !massage) return;
        if(image){
            const uploadResponse = await uploadimage(image, appConfig.POSTS_PHOTOS_BUCKET);
            if(currentPost.imgUrl !== ""&& currentPost.imgKey !== ""){
                await deleteImage(post.imgKey, appConfig.POSTS_PHOTOS_BUCKET);
            }
            currentPost.imgUrl = uploadResponse.Location;
            currentPost.imgKey = uploadResponse.Key
            //await Storage.put(filename, image);    
        }
        currentPost.username = user.username;
        const result = await updatePost(currentPost);
        console.log("new post"+result);
        nav(`/view-posts/${currentPost.id}`);
    }

  

    async function uploadImage() {
        imageFileInput.current.click();
    }
    function handleChange(e){
        const fileUploaded = e.target.files[0];
        if(!fileUploaded) return null;
        setImage(fileUploaded);
    }

    if (user.username==="User") {
        return (<div className="text-3xl font-semibold tracking-wide mt-6 ">Plese Sign in</div>)
    } 

if(isLoading){
    return(<div className="text-3xl font-semibold tracking-wide mt-6 ">Loding......</div>)
}

    return (
        <div>
            {
                isupdatePost?(

                    <h1 className="text-3xl font-semibold tracking-wide mt-6">
                    Edit This Post
                    </h1>
                ):(
                    
                    <h1 className="text-3xl font-semibold tracking-wide mt-6">
                    Create New Post
                    </h1>
                )
            }
            
            {
                (currentPost.imgUrl || image)&& (
                    <img src={image ? URL.createObjectURL(image):currentPost.imgUrl}
                    className="my-4" alt=""/>
                )
            }

            <input onChange={onChange}
            name="title"
            placeholder="Title"
            value={currentPost.title}
            className="border-b pb-2 text-lg my-4 
            focus:outline-none font-light text-gray-500 placeholder-gray-500 y-2 w-1/2"
            />
            
            <SimpleMdeReact className="w-1/2 h-1/2"
            value={currentPost.massage}
            onChange={(value)=> setPost({...currentPost, massage:value})}
            />
            <input type='file' 
            ref={imageFileInput}
            className="absolute w-0 h-0"
            onChange={handleChange}/>

            {
                isupdatePost?(
                    <button
                    type="button"
                    className="mb-4 bg-blue-600 text-white 
                    font-semibold px-8 py-2 rounded-lg"
                    onClick={updatecurrentPost}>
                        Update Post
                    </button>
                ):(

                    <button
                    type="button"
                    className="mb-4 bg-blue-600 text-white 
                    font-semibold px-8 py-2 rounded-lg"
                    onClick={createNewPost}>
                        Create Post
                    </button>
                )   
            }
            <button
            type="button"
            className="mb-4 bg-green-600 text-white 
            font-semibold px-8 py-2 rounded-lg ml-2"
            onClick={uploadImage}>
                Upload cover image 
            </button>
        </div>
    )
}

//{"  "} could be used to put space between two component

export default CreatePost;