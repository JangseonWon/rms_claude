import type {Request} from "@/model/Request";

export async function patchRequests(requests: Request[]) {
    return await fetch(`/w-api/result-service/requests`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
}