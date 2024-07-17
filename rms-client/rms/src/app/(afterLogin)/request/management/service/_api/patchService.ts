import {Service} from "@/model/Service";

export async function patchService(service: Service) {
    return await fetch(`/w-api/management-service/services/${service.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(service),
        credentials: 'include',
        cache: 'no-store'
    });
}