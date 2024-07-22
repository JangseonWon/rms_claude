import {Service} from "@/model/Service";

export async function putUserService(userId: string, service: Service[]) {
    return await fetch(`/w-api/management-service/users/${userId}/services`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(service),
        credentials: 'include',
        cache: 'no-store'
    });
}