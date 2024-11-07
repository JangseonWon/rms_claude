export async function deleteCommentById(id: String) {
    return await fetch(`/w-api/post-service/comment/${id}`, {
        method: 'DELETE',
        credentials: 'include',
        cache: 'no-store'
    });
}