export async function deleteCommentById(id: String) {
    const res = await fetch(`/w-api/post-service/comment/${id}`, {
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