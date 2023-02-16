import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import ServiceContext from '../context/ServiceContext';
const img = require("../img/favicon.ico");


function NavBar() {
const {user, setMyPostPage} = useContext(ServiceContext);

  return (
    <div className='flex flex-row pt-3 pb-3 border-b  
    bg-cyan-500 border-gray-300 w-full'>
        <div className='ml-8 space-x-4 flex-1'>
            <Link to="/" className='rounded-lg hover:bg-slag-100
                            hover:text-slate-900
                            text-slate-700 font-medium mr-2' onClick={()=>setMyPostPage(false)}>Home</Link>
            <Link to="/create-post" className='rounded-lg hover:bg-slag-100
                            hover:text-slate-900 
                            text-slate-700 font-medium mr-2'>Create Post</Link>
            {
                (user.username!=="User") && (<Link to={`/my-posts/${user.username}`} className='rounded-lg hover:bg-slag-100
                hover:text-slate-900 
                text-slate-700 font-medium mr-2' onClick={()=>setMyPostPage(true)} >My Posts</Link>)
            }
            
            
        </div>
        <div className='float-right'>
            <div className='flex'>
                <div className='rounded-lg mt-5px
                            text-slate-700 font-medium mr-2'
                            style={{pointerEvents:"none"}}>{user.username}
                </div>

                <Link to='/profile' > 
                { (user.username !=="User" && user.username!==null) ? ( <img src={user.attributes.picture?user.attributes.picture :img}
                        className="w-9 h-9 bg-contain bg-center 
                        rounded-full sm:mx-0 sm:shrink-0 " alt=""/>): 
                        (<button className="px-4 py-1 
                        text-sm text-red-600 font-semibold 
                        rounded-full border border-purple-200 
                        hover:text-white hover:bg-green-600 hover:border-transparent 
                        focus:outline-none focus:ring-2 focus:ring-green-600 
                        focus:ring-offset-2">Login</button>)

                }
                </Link>
            </div>
        </div>
    </div>
  )
}


export default NavBar