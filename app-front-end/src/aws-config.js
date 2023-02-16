

const apiUrl = 'https://4vj50o8jt1.execute-api.ap-south-1.amazonaws.com/prod'

export const config = {
    REGION: 'ap-south-1',
    USER_POOL_ID: 'ap-south-1_3D2FCta95',
    APP_CLIENT_ID: '4mkprl73vdjb48kfg8qtm29ro5',
    IDENTITY_POOL_ID: 'ap-south-1:3d40c022-cd9b-4e44-ab92-70ffdda8c59a',
    POSTS_PHOTOS_BUCKET: 'posts-photos-0a8c1cd4e120',
    PROFILE_PHOTOS_BUCKET: 'profile-photos-0a8c1cd4e120',
    api: {
        name: "NewApi",
        baseUrl: apiUrl,
        postsUrl: `${apiUrl}/posts/`,
        postsByUsernameUrl: `${apiUrl}/posts?username=`
        // commentsUrl: `${apiUrl}comments/`
    }
}