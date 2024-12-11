import {Comment} from "@/model/Comment";

export async function putComment(postId: number, comment: Comment) {
    const res = await fetch(`/w-api/post-service/post/${postId}/comment`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(comment),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}