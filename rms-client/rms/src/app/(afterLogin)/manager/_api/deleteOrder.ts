import type {Request} from "@/model/Request";

export async function deleteOrder(request: Request[]) {
    return await fetch(`/w-api/management-service/requests`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
}