import React, { useEffect, useState, useContext } from 'react'
import PostCard from './PostCard'
import { useParams } from 'react-router-dom'
import ServiceContext from '../context/ServiceContext';


function HomePage() {

    const {posts, getPostsByUserName, getPosts, deletePostById, isLoading, getPostById, isAdmin, myPost} = useContext(ServiceContext);
    const params = useParams();
    const [hedder, setHedder] = useState("Welcome To Home Page");
    const [hidden, sethidden] = useState(true);
    const usernameParam = params.username ;

    useEffect(()=>{
      if (myPost){
        setHedder("Welcome To My Post Page "+usernameParam)
        sethidden(false);
        getPostsByUserName();
      } else {
        setHedder("Welcome To Home Page")
        getPosts();
        sethidden(!isAdmin);
      }
    },[myPost])
    

  if(isLoading){
      return(<div className="text-3xl font-semibold tracking-wide mt-6 ">Loding......</div>)
   }

  if(posts==="No Data Found"){
    return(<div className="text-3xl font-semibold tracking-wide mt-6 " >No Data Found.....Please create new Data</div>)
  }
  
  return (
    <div>
        <h1 className="text-3xl font-semibold tracking-wide mt-6  mb-10">{hedder}</h1>
      {posts && posts.map((item, index)=>(
        <div key={index} className="w-1/2">
        <PostCard    title={item.title} 
        massage={item.massage} 
        ImgUrl={item.imgUrl} 
        username={item.username} 
        hidden={hidden} 
        id={item.id}
        imgKey={item.imgKey} 
        getPostById={getPostById}
        deleteCurrentPost={deletePostById}/>
        </div>
      ))}
    </div>
  )
}

export default HomePage