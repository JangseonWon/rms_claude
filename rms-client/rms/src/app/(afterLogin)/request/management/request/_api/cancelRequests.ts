import type {Request} from "@/model/Request";

export async function cancelRequests(requests: Request[]) {
    return await fetch(`/w-api/management-service/requests`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
}