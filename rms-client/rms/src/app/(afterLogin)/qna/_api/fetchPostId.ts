export async function fetchPostId(postId: String, check: boolean) {
    return await fetch(`/w-api/post-service/post/${postId}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(check),
        credentials: 'include',
        cache: 'no-store'
    });
}