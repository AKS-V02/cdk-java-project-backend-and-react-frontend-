import React, { useContext, useRef, useState} from 'react'
import { withAuthenticator } from "@aws-amplify/ui-react";
import ServiceContext from '../context/ServiceContext';
const img = require("../img/favicon.ico");

function MyProfile() {
  const {user , signOut, isLoading, updateUserProfilePicture} = useContext(ServiceContext);
  const imageFileInput = useRef(null);
  const [image, setImage] = useState(null);
  
  
    
    async function uploadImage() {
      imageFileInput.current.click();
  }
  function handleChange(e){
      const fileUploaded = e.target.files[0];
      if(!fileUploaded) return null;
      setImage(fileUploaded);
      updateUserProfilePicture(fileUploaded);
  }
    

    if (user.username==="User") {
        return (<div className="text-3xl font-semibold tracking-wide mt-6 ">Plese Sign in</div>)
    }
  if(isLoading){
      return(<div className="text-3xl font-semibold tracking-wide mt-6 ">Loding......</div>)
   }



  return (
    <div>
    <h1 className="text-3xl font-semibold tracking-wide mt-6 mb-10 ">
    Profile
    </h1>
    {user.attributes.picture ? (
      <img
        className='w-36 h-36 bg-contain bg-center rounded-full sm:mx-0 sm:shrink-0 ml-10'
        src={image ? URL.createObjectURL(image):user.attributes.picture}
      alt="N/A" />
    ):(
      <img
        className='w-36 h-36 bg-contain bg-center rounded-full sm:mx-0 sm:shrink-0 ml-10'
        src={img}
      alt="N/A" />
    )
  }
    <h1 className="font-medium text-gray-500 my-2 ml-10">
        {user.username}
    </h1>
    <p className="text-sm text-gray-500 mb-1 ml-10">
      {user.attributes.email}  
    </p>

    <input type='file' 
            ref={imageFileInput}
            className="absolute w-0 h-0"
            onChange={handleChange}/>

    <button
        type="button"
        className="px-8 py-2 text-sm text-red-600 font-semibold rounded-lg border border-black-200 
        hover:text-white hover:bg-red-600 hover:border-transparent focus:outline-none 
        focus:ring-2 focus:ring-purple-600 focus:ring-offset-2 ml-4"
        onClick={signOut}
        >Sign Out</button>

          <button
            type="button"
            className="px-8 py-2 text-sm text-green-600 font-semibold rounded-lg border border-black-200 
            hover:text-white hover:bg-green-600 hover:border-transparent focus:outline-none 
            focus:ring-2 focus:ring-purple-600 focus:ring-offset-2 ml-4"
            onClick={uploadImage}>
                Upload profile image 
            </button>
</div>
  )
}

export default withAuthenticator(MyProfile)