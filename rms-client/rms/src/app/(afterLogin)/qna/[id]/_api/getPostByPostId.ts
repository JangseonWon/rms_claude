export async function getPostByPostId(postId: number) {
    const res = await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}