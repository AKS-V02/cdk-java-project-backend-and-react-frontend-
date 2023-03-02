import React, { useContext, useRef, useState} from 'react'
import { withAuthenticator } from "@aws-amplify/ui-react";
import ServiceContext from '../context/ServiceContext';
const img = require("../img/favicon.ico");

function MyProfile() {
  const {user , signOut, isLoading, updateUserProfilePicture, vrifyUserEmailSubmit,vrifyUserEmail,updateUserAttributes} = useContext(ServiceContext);
  const imageFileInput = useRef(null);
  const [image, setImage] = useState(null);
  const [code, setCode] = useState("");
  const [iscode, setIsCode] = useState(true);
  const [isEdit, setIsEdit] = useState(true);
  const [attribute, setAttribute] = useState({email:""});

  function onChange(e){
    setCode(e.target.value);
    }
  function onChangeEmail(e){
    setAttribute({email: e.target.value})
  }
    
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
    <div className="mb-5 ml-10 flex">
    <p className='text-sm text-gray-500 py-1'>{user.attributes.email}</p>
    <p onClick={()=>setIsEdit(!isEdit)} className='py-1 text-sm text-blue-500 ml-2 cursor-pointer'>Edit email</p>
    
    {user.attributes.email_verified? (
      <p className='text-sm text-green-600 ml-2 pointer-events-none py-1'>varified</p>): 

      <p onClick={()=>{setIsCode(false);vrifyUserEmail()}} className='py-1 text-sm text-red-600 ml-2 cursor-pointer'>not varified</p>}
    
        <div hidden={iscode} >
          <input onChange={onChange}
                  name="code"
                  placeholder="Varification Code"
                  value={code}
                  className="border-b text-lg ml-5
                  focus:outline-none font-light text-gray-500 placeholder-gray-500 w-auto rounded-lg"
                  />
          <button type="button"
            className="px-8 py-2 text-sm text-green-600 font-semibold rounded-lg border border-black-200 
            hover:text-white hover:bg-green-600 hover:border-transparent focus:outline-none 
            focus:ring-2 focus:ring-purple-600 focus:ring-offset-2 ml-4"
            onClick={()=>{vrifyUserEmailSubmit(code);setIsCode(true)}}>Submit Code</button>

        </div>
    </div>

    <div hidden={isEdit} className="mb-5">
    <input onChange={onChangeEmail}
                  name="email"
                  placeholder="New Email"
                  value={attribute.email}
                  className="border-b text-lg ml-5
                  focus:outline-none font-light text-gray-500 placeholder-gray-500 w-1/4 rounded-lg"
                  />
          <button type="button"
            className="px-8 py-2 text-sm text-green-600 font-semibold rounded-lg border border-black-200 
            hover:text-white hover:bg-green-600 hover:border-transparent focus:outline-none 
            focus:ring-2 focus:ring-purple-600 focus:ring-offset-2 ml-4"
            onClick={()=>{updateUserAttributes(attribute);setIsEdit(true);setIsCode(false)}}>Update Email</button>
    </div>

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