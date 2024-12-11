export async function deletePostFileById(postFileId: string) {
    const res = await fetch(`/w-api/post-service/post-files/${postFileId}`, {
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