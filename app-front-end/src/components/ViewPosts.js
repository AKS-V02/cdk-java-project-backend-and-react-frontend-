
import { ReactMarkdown } from "react-markdown/lib/react-markdown"
import React, {  useContext} from 'react'
import ServiceContext from '../context/ServiceContext';

function ViewPosts() {
  const {user , post, isLoading} = useContext(ServiceContext);


  if (user.username==="User") {
      return (<div className="text-3xl font-semibold tracking-wide mt-6 ">Plese Sign in</div>)
  } 
  if(isLoading){
    return(<div className="text-3xl font-semibold tracking-wide mt-6 ">Loding......</div>)
 }
  return (
    <>
    {
      post && (
  
        <div className=" border-b border-gray-300
          mt-8 pb-4">
            <p className="text-xl font-semibold text-green-500">{post.title}</p>
            <p className="text-gray-500 mt-2 mb-4" >Author: {user.username}</p>
            {
              post.imgUrl!=="" && (
                <img src={post.imgUrl} className="w-36 h-36 bg-contain bg-center 
                rounded-full sm:mx-0 sm:shrink-0" alt=''/>
              )
            }
            <ReactMarkdown className="prose text-white mt-10" children={post.massage} />
      </div>
      ) 
    }
    </>
  )
}


export default ViewPosts