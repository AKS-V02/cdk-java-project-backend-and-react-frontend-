import React from 'react'
import { ReactMarkdown } from "react-markdown/lib/react-markdown"
import { Link } from 'react-router-dom'


function PostCard({title, massage, ImgUrl, username, hidden, id, deleteCurrentPost, getPostById, imgKey}) {
 
 
  return (
    <div
    className='py-8 px-8 max-w-xxl mx-auto bg-white rounded-xl shadow-lg space-y-2 sm:py-1 sm:flex 
    sm:items-center sm:space-y-0 sm:space-x-6 mb-2'
  >
    {ImgUrl && (
      <img
        className='w-36 h-36 bg-contain bg-center rounded-full sm:mx-0 sm:shrink-0'
        src={ImgUrl}
      alt="" />
    )}
    <div className='text-center space-y-2 sm:text-left'>
      <div className='space-y-0.5'>
        <p className='text-lg text-black font-semibold'>{title}</p>
        <p className="text-gray-500 mt-2 mb-4" >Author: {username}</p>
        <ReactMarkdown className="prose" children={massage} />
        {/* <p className='text-slate-500 font-medium'>
          Created on: {Moment(post.createdAt).format("ddd, MMM hh:mm a")}
        </p> */}
      </div>
      <div
        className='sm:py-4 sm:flex 
  sm:items-center sm:space-y-0 sm:space-x-1'
      >
        <button
          className='px-4 py-1 text-sm text-purple-600 font-semibold rounded-full border border-purple-200 
hover:text-white hover:bg-purple-600 hover:border-transparent focus:outline-none 
focus:ring-2 focus:ring-purple-600 focus:ring-offset-2'
        onClick={()=>getPostById(id)}>
          <Link to={`/view-posts/${id}`}>View Post</Link>
        </button>
        {
          !hidden && (
            <>
            <button
              className='px-4 py-1 text-sm text-purple-600 font-semibold rounded-full border border-purple-200 
    hover:text-white hover:bg-purple-600 hover:border-transparent focus:outline-none 
    focus:ring-2 focus:ring-purple-600 focus:ring-offset-2'
           onClick={()=>getPostById(id)} >
              <Link to={`/edit-post/${id}`} >Edit Post</Link>
            </button>


            <button 
              className='text-sm mr-4 text-red-500'
              onClick={() => deleteCurrentPost(id, imgKey)}
            >
              
              Delete Post
            </button>
            </>
          )
        }
      </div>
    </div>
  </div>
  )
}



export default PostCard