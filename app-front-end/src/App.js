
import { Route, BrowserRouter as  Router, Routes } from 'react-router-dom';

import CreatePost from './components/createPosts';

import NavBar from './components/NavBar';
import HomePage from './components/HomePage';
import MyProfile from './components/MyProfile';
import Footer from './components/Footer';
import Error from './components/Error';
import ViewPosts from './components/ViewPosts';
import { ServiceProvider } from './context/ServiceContext';
// Amplify.configure({
//   aws_cognito_region: config.REGION, // (required) - Region where Amazon Cognito project was created
//   aws_user_pools_id: config.USER_POOL_ID, // (optional) -  Amazon Cognito User Pool ID
//   aws_user_pools_web_client_id: config.APP_CLIENT_ID, // (optional) - Amazon Cognito App Client ID (App client secret needs to be disabled)
//   aws_cognito_identity_pool_id: config.IDENTITY_POOL_ID, // (optional) - Amazon Cognito Identity Pool ID
//   aws_mandatory_sign_in: 'enable', // (optional) - Users are not allowed to get the aws credentials unless they are signed in
//   aws_cognito_verification_mechanisms: ["EMAIL"],
//   aws_cognito_signup_attributes: ["EMAIL"],
//   API:{
//     endpoints:[
//       {
//         name: config.api.name,
//         endpoint: config.api.baseUrl
//       }
//     ]
//   }

// });


function App() {

  return (
  <ServiceProvider>
    <Router>
      <div className="flex flex-col justify-between h-screen">
      <NavBar/>
      <div className='flex-auto bg-slate-800'>
        <Routes>
          <Route exact path='/' element={<HomePage />} />
          <Route exact path='/create-post' element={<CreatePost/>} />
          <Route exact path='/my-posts/:username' element={<HomePage/>} />
          <Route exact path='/edit-post/:id' element={<CreatePost/>} />
          <Route exact path='/profile' element={<MyProfile/>} />
          <Route exact path='/view-posts/:id' element={<ViewPosts/>} />
          <Route exact path='/error' element={<Error/>} />
          
        </Routes>
        </div>
        <Footer/>
      </div>
    </Router>
  </ServiceProvider>
  );
}

export default App;
