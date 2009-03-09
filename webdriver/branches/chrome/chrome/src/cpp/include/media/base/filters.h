// Copyright (c) 2006-2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Filters are connected in a strongly typed manner, with downstream filters
// always reading data from upstream filters.  Upstream filters have no clue
// who is actually reading from them, and return the results via OnAssignment
// using the AssignableInterface<SomeBufferType> interface:
//
//                         DemuxerStream(Video) <- VideoDecoder <- VideoRenderer
// DataSource <- Demuxer <
//                         DemuxerStream(Audio) <- AudioDecoder <- AudioRenderer
//
// Upstream -------------------------------------------------------> Downstream
//                         <- Reads flow this way
//                    Buffer assignments flow this way ->
//
// Every filter maintains a reference to the scheduler, who maintains data
// shared between filters (i.e., reference clock value, playback state).  The
// scheduler is also responsible for scheduling filter tasks (i.e., a read on
// a VideoDecoder would result in scheduling a Decode task).  Filters can also
// use the scheduler to signal errors and shutdown playback.

#ifndef MEDIA_BASE_FILTERS_H_
#define MEDIA_BASE_FILTERS_H_

#include <limits>
#include <string>

#include "base/logging.h"
#include "base/ref_counted.h"
#include "base/time.h"
#include "media/base/media_format.h"

namespace media {

template <class TBuffer> class Assignable;
class Buffer;
class Decoder;
class DemuxerStream;
class FilterHost;
class VideoFrame;
class WritableBuffer;

// Identifies the type of filter implementation.  Used in conjunction with some
// template wizardry to enforce strongly typed operations.  More or less a
// knock off of MSVC's __uuidof() operator.
enum FilterType {
  FILTER_DATA_SOURCE,
  FILTER_DEMUXER,
  FILTER_AUDIO_DECODER,
  FILTER_VIDEO_DECODER,
  FILTER_AUDIO_RENDERER,
  FILTER_VIDEO_RENDERER
};


class MediaFilter : public base::RefCountedThreadSafe<MediaFilter> {
 public:
  MediaFilter() : host_(NULL) {}

  // Sets the protected member |host_|. This is the first method called by
  // the FilterHost after a filter is created.  The host holds a strong
  // reference to the filter.  The refernce held by the host is guaranteed
  // to be released before the host object is destroyed by the pipeline.
  virtual void SetFilterHost(FilterHost* host) {
    DCHECK(NULL == host_);
    DCHECK(NULL != host);
    host_ = host;
  }

  // The pipeline is being stopped either as a result of an error or because
  // the client called Stop().
  virtual void Stop() = 0;

  // The pipeline playback rate has been changed.  Filters may implement this
  // method if they need to respond to this call.
  virtual void SetPlaybackRate(float playback_rate) {}

  // The pipeline is being seeked to the specified time.  Filters may implement
  // this method if they need to respond to this call.
  virtual void Seek(base::TimeDelta time) {}

 protected:
  FilterHost* host_;
  friend class base::RefCountedThreadSafe<MediaFilter>;
  virtual ~MediaFilter() {}

 private:
  DISALLOW_COPY_AND_ASSIGN(MediaFilter);
};


class DataSource : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_DATA_SOURCE;
  }

  static bool IsMediaFormatSupported(const MediaFormat* media_format) {
    std::string mime_type;
    return (media_format->GetAsString(MediaFormat::kMimeType, &mime_type) &&
            mime_type == mime_type::kURL);
  }

  static const size_t kReadError = static_cast<size_t>(-1);

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(const std::string& url) = 0;

  // Returns the MediaFormat for this filter.
  virtual const MediaFormat* GetMediaFormat() = 0;

  // Read the given amount of bytes into data, returns the number of bytes read
  // if successful, kReadError otherwise.
  virtual size_t Read(uint8* data, size_t size) = 0;

  // Returns true and the current file position for this file, false if the
  // file position could not be retrieved.
  virtual bool GetPosition(int64* position_out) = 0;

  // Returns true if the file position could be set, false otherwise.
  virtual bool SetPosition(int64 position) = 0;

  // Returns true and the file size, false if the file size could not be
  // retrieved.
  virtual bool GetSize(int64* size_out) = 0;
};


class Demuxer : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_DEMUXER;
  }

  static bool IsMediaFormatSupported(const MediaFormat* media_format) {
    std::string mime_type;
    return (media_format->GetAsString(MediaFormat::kMimeType, &mime_type) &&
            mime_type == mime_type::kApplicationOctetStream);
  }

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(DataSource* data_source) = 0;

  // Returns the number of streams available
  virtual size_t GetNumberOfStreams() = 0;

  // Returns the stream for the given index, NULL otherwise
  virtual DemuxerStream* GetStream(int stream_id) = 0;
};


class DemuxerStream {
 public:
  // Returns the MediaFormat for this filter.
  virtual const MediaFormat* GetMediaFormat() = 0;

  // Schedules a read and takes ownership of the given buffer.
  virtual void Read(Assignable<Buffer>* buffer) = 0;

 protected:
  virtual ~DemuxerStream() {}
};


class VideoDecoder : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_VIDEO_DECODER;
  }

  static const char* major_mime_type() {
    return mime_type::kMajorTypeVideo;
  }

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(DemuxerStream* demuxer_stream) = 0;

  // Returns the MediaFormat for this filter.
  virtual const MediaFormat* GetMediaFormat() = 0;

  // Schedules a read and takes ownership of the given buffer.
  virtual void Read(Assignable<VideoFrame>* video_frame) = 0;
};


class AudioDecoder : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_AUDIO_DECODER;
  }

  static const char* major_mime_type() {
    return mime_type::kMajorTypeAudio;
  }

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(DemuxerStream* demuxer_stream) = 0;

  // Returns the MediaFormat for this filter.
  virtual const MediaFormat* GetMediaFormat() = 0;

  // Schedules a read and takes ownership of the given buffer.
  virtual void Read(Assignable<Buffer>* buffer) = 0;
};


class VideoRenderer : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_VIDEO_RENDERER;
  }

  static const char* major_mime_type() {
    return mime_type::kMajorTypeVideo;
  }

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(VideoDecoder* decoder) = 0;
};


class AudioRenderer : public MediaFilter {
 public:
  static const FilterType filter_type() {
    return FILTER_AUDIO_RENDERER;
  }

  static const char* major_mime_type() {
    return mime_type::kMajorTypeAudio;
  }

  // Initializes this filter, returns true if successful, false otherwise.
  virtual bool Initialize(AudioDecoder* decoder) = 0;

  // Sets the output volume.
  virtual void SetVolume(float volume) = 0;
};

}  // namespace media

#endif  // MEDIA_BASE_FILTERS_H_
