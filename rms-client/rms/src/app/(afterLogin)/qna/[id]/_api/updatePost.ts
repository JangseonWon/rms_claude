import {Post} from "@/model/Post";

export async function updatePost(post: Post, files: File[]) {
    const formData: FormData = new FormData()
    files.forEach(file => {
        formData.append('file', file);
    });
    formData.append('data', JSON.stringify(post))
    return await fetch(`/w-api/post-service/post/${post.id}`, {
        method: 'PATCH',
        body: formData,
        credentials: 'include',
        cache: 'no-store'
    });
}