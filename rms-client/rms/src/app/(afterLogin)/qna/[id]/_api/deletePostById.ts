export async function deletePostById(postId: number) {
    const res = await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}