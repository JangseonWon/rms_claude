export async function getPostByPostId(postId: String) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}