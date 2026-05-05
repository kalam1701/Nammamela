package com.nammamela.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nammamela.data.model.*
import kotlinx.coroutines.tasks.await

class NammaMelaRepository {
    private val db = FirebaseFirestore.getInstance()

    // Play
    private val _tonightsPlay = MutableLiveData<Play?>()
    val tonightsPlay: LiveData<Play?> = _tonightsPlay

    init {
        // Listen to active play
        db.collection("plays").whereEqualTo("active", true).limit(1)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val play = snapshot.documents[0].toObject(Play::class.java)
                    if (play != null) {
                        play.id = snapshot.documents[0].id
                        _tonightsPlay.postValue(play)
                    }
                } else {
                    _tonightsPlay.postValue(null)
                }
            }

        // Listen to all seats
        db.collection("seats").orderBy("row").orderBy("seatNumber")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val seats = snapshot.documents.mapNotNull {
                        val seat = it.toObject(Seat::class.java)
                        seat?.id = it.id
                        seat
                    }
                    _allSeats.postValue(seats)
                    _availableCount.postValue(seats.count { it.status == SeatStatus.AVAILABLE.name })
                    _totalCount.postValue(seats.size)
                }
            }

        // Listen to cast members
        db.collection("cast")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val castList = snapshot.documents.mapNotNull {
                        val c = it.toObject(CastMember::class.java)
                        c?.id = it.id
                        c
                    }
                    _allCast.postValue(castList)
                }
            }

        // Listen to fan posts
        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull {
                        val p = it.toObject(FanPost::class.java)
                        p?.id = it.id
                        p
                    }
                    _allPosts.postValue(posts)
                    _postCount.postValue(posts.size)
                }
            }
    }

    suspend fun savePlay(play: Play) {
        // deactivate old ones
        val activePlays = db.collection("plays").whereEqualTo("active", true).get().await()
        db.runBatch { batch ->
            for (doc in activePlays.documents) {
                batch.update(doc.reference, "active", false)
            }
            val newRef = db.collection("plays").document()
            batch.set(newRef, play.copy(isActive = true))
        }.await()
    }

    suspend fun updatePlay(play: Play) {
        db.collection("plays").document(play.id).set(play).await()
    }

    suspend fun getTonightsPlayOnce(): Play? {
        val snapshot = db.collection("plays").whereEqualTo("active", true).limit(1).get().await()
        if (!snapshot.isEmpty) {
            val play = snapshot.documents[0].toObject(Play::class.java)
            play?.id = snapshot.documents[0].id
            return play
        }
        return null
    }

    // Seats
    private val _allSeats = MutableLiveData<List<Seat>>()
    val allSeats: LiveData<List<Seat>> = _allSeats

    private val _availableCount = MutableLiveData<Int>()
    val availableCount: LiveData<Int> = _availableCount

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    suspend fun initSeatsIfNeeded() {
        val snapshot = db.collection("seats").limit(1).get().await()
        if (snapshot.isEmpty) {
            val rows = listOf("A","B","C","D","E","F","G","H","I","J")
            db.runBatch { batch ->
                for (row in rows) {
                    for (num in 1..12) {
                        val ref = db.collection("seats").document()
                        batch.set(ref, Seat(row = row, seatNumber = num, status = SeatStatus.AVAILABLE.name, seatLabel = "$row-$num"))
                    }
                }
            }.await()
        }
    }

    suspend fun reserveSeat(seatId: String, name: String) {
        db.collection("seats").document(seatId)
            .update(mapOf("status" to SeatStatus.RESERVED.name, "bookedByName" to name)).await()
    }

    suspend fun resetAllSeats() {
        val snapshot = db.collection("seats").get().await()
        db.runBatch { batch ->
            for (doc in snapshot.documents) {
                batch.update(doc.reference, mapOf("status" to SeatStatus.AVAILABLE.name, "bookedByName" to ""))
            }
        }.await()
    }

    // Cast
    private val _allCast = MutableLiveData<List<CastMember>>()
    val allCast: LiveData<List<CastMember>> = _allCast

    suspend fun insertCastMember(castMember: CastMember) {
        db.collection("cast").add(castMember).await()
    }

    suspend fun updateCastMember(castMember: CastMember) {
        db.collection("cast").document(castMember.id).set(castMember).await()
    }

    suspend fun deleteCastMember(castMember: CastMember) {
        db.collection("cast").document(castMember.id).delete().await()
    }

    suspend fun initCastIfNeeded() {
        val snapshot = db.collection("cast").limit(1).get().await()
        if (snapshot.isEmpty) {
            db.runBatch { batch ->
                batch.set(db.collection("cast").document(), CastMember(name = "Rajkumar Rao", role = "Lead Actor", bio = "Veteran stage artist with 20 years of Company Nataka experience.", photoUrl = ""))
                batch.set(db.collection("cast").document(), CastMember(name = "Siddappa Hasyagar", role = "Comedian", bio = "The crowd's favourite! Known for his lightning-fast wit.", photoUrl = ""))
                batch.set(db.collection("cast").document(), CastMember(name = "Kavitha Suresh", role = "Singer", bio = "Classical vocalist who sets the emotional tone of every act.", photoUrl = ""))
            }.await()
        }
    }

    // Fan Wall
    private val _allPosts = MutableLiveData<List<FanPost>>()
    val allPosts: LiveData<List<FanPost>> = _allPosts

    private val _postCount = MutableLiveData<Int>()
    val postCount: LiveData<Int> = _postCount

    suspend fun addPost(post: FanPost) {
        db.collection("posts").add(post).await()
    }

    suspend fun deletePost(id: String) {
        db.collection("posts").document(id).delete().await()
    }
}
