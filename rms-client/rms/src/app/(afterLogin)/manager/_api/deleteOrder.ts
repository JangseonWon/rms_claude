import type {Request} from "@/model/Request";

export async function deleteOrder(request: Request[]) {
    const res = await fetch(`/w-api/management-service/requests`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}